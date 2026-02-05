package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Checkins;

import jakarta.persistence.*;

@Entity
@Table(name = "trainer_checkin_questions")
public class TrainerCheckInQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(nullable = false, length = 300)
    private String prompt;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(name = "required", nullable = false)
    private boolean required = true;

    public Long getId() {
        return id;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
