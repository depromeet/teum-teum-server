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
import net.teumteum.alert.domain.Alert;
import net.teumteum.alert.domain.AlertPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class FcmAlertPublisher implements AlertPublisher {

    private static final int MAX_RETRY_COUNT = 5;
    private static final String FCM_TOKEN_PATH = "teum-teum-12611-firebase-adminsdk-cjyx3-ea066f25ef.json";

    @Override
    @Async(FCM_ALERT_EXECUTOR)
    public void publish(String token, Alert alert) {
        var message = buildMessage(token, alert);
        publishWithRetry(0, message, null);
    }

    private Message buildMessage(String token, Alert alert) {
        return Message.builder()
            .setToken(token)
            .setNotification(buildNotification(alert))
            .setAndroidConfig(buildAndroidConfig(alert))
            .putData("publishedAt", alert.getCreatedAt().toString())
            .putData("userId", alert.getUserId().toString())
            .putData("type", alert.getType().toString())
            .build();
    }

    private void publishWithRetry(int currentRetryCount, Message message, @Nullable ErrorCode errorCode) {
        if (MAX_RETRY_COUNT == currentRetryCount) {
            return;
        }
        if (errorCode == null
            || errorCode == ErrorCode.INTERNAL
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

    private Notification buildNotification(Alert alert) {
        return Notification.builder()
            .setTitle(alert.getTitle())
            .setBody(alert.getBody())
            .build();
    }

    private AndroidConfig buildAndroidConfig(Alert alert) {
        return AndroidConfig.builder()
            .setNotification(AndroidNotification.builder()
                .setTitle(alert.getTitle())
                .setBody(alert.getBody())
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
