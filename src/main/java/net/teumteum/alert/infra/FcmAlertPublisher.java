package net.teumteum.alert.infra;

import static net.teumteum.alert.infra.FcmAlertExecutorConfigurer.FCM_ALERT_EXECUTOR;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.ErrorCode;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import net.teumteum.alert.domain.AlertPublisher;
import net.teumteum.alert.domain.BeforeMeetingAlert;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class FcmAlertPublisher implements AlertPublisher<BeforeMeetingAlert> {

    private static final int MAX_RETRY_COUNT = 5;
    private static final String FCM_TOKEN_PATH = "teum-teum-12611-firebase-adminsdk-cjyx3-ea066f25ef.json";

    @Override
    @Async(FCM_ALERT_EXECUTOR)
    public void publish(BeforeMeetingAlert beforeMeetingAlert) {
        System.out.println(">>> publish(" + beforeMeetingAlert + ")");
        var message = buildMessage(beforeMeetingAlert);
        publishWithRetry(0, message, null);
    }

    private void publishWithRetry(int currentRetryCount, Message message, @Nullable ErrorCode errorCode) {
        System.out.println(">>> publishWithRetry(" + currentRetryCount + ", " + message + ", " + errorCode + ")");
        if (MAX_RETRY_COUNT == currentRetryCount) {
            return;
        }
        if (errorCode == ErrorCode.INTERNAL
            || errorCode == ErrorCode.CONFLICT
            || errorCode == ErrorCode.UNKNOWN
            || errorCode == ErrorCode.DATA_LOSS) {
            try {
                FirebaseMessaging.getInstance().send(message);
            } catch (FirebaseMessagingException firebaseMessagingException) {
                publishWithRetry(currentRetryCount + 1, message, firebaseMessagingException.getErrorCode());
            }
        }
    }

    private Message buildMessage(BeforeMeetingAlert beforeMeetingAlert) {
        return Message.builder()
            .setToken(beforeMeetingAlert.token())
            .setNotification(buildNotification(beforeMeetingAlert))
            .setAndroidConfig(buildAndroidConfig(beforeMeetingAlert))
            .putData("publishedAt", beforeMeetingAlert.publishedAt().toString())
            .putData("userId", beforeMeetingAlert.userId().toString())
            .build();
    }

    private Notification buildNotification(BeforeMeetingAlert beforeMeetingAlert) {
        return Notification.builder()
            .setTitle(beforeMeetingAlert.title())
            .setBody(beforeMeetingAlert.body())
            .build();
    }

    private AndroidConfig buildAndroidConfig(BeforeMeetingAlert beforeMeetingAlert) {
        return AndroidConfig.builder()
            .setNotification(AndroidNotification.builder()
                .setTitle(beforeMeetingAlert.title())
                .setBody(beforeMeetingAlert.body())
                .setClickAction("push_click")
                .build())
            .build();
    }

    @PostConstruct
    private void fcmCredential() {
        try {
            var resource = new ClassPathResource(FCM_TOKEN_PATH);
            resource.getInputStream();

            var firebaseOptions = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .build();

            FirebaseApp.initializeApp(firebaseOptions);
        } catch (IOException ioException) {
            throw new IllegalStateException("애플리케이션을 시작할 수 없습니다.", ioException);
        }
    }
}
