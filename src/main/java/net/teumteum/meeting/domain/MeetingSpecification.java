package net.teumteum.meeting.domain;

import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MeetingSpecification {

    public static Specification<Meeting> withIsOpen(boolean isOpen) {
        if (isOpen) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("promiseDateTime"), LocalDateTime.now());
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("promiseDateTime"), LocalDateTime.now());
    }

    public static Specification<Meeting> withTopic(Topic topic) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("topic"), topic);
    }

    public static Specification<Meeting> withAreaStreet(String meetingAreaStreet) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("meetingArea").get("mainStreet"), meetingAreaStreet);
    }

    public static Specification<Meeting> withSearchWordInTitle(String searchWord) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + searchWord + "%");
    }

    public static Specification<Meeting> withSearchWordInIntroduction(String searchWord) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("introduction"), "%" + searchWord + "%");
    }

    public static Specification<Meeting> withParticipantUserId(Long participantUserId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("participantUserIds"), participantUserId);
    }

}
