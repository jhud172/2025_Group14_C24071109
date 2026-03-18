package uk.ac.cf._5.group14.One_To_One.Chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "daily_usage")
@IdClass(DailyUsageId.class)
@Getter
@Setter
public class DailyUsage {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "usage_date", nullable = false)
    private LocalDate date;

    @Column(name = "used_count", nullable = false)
    private int usedCount;
}
