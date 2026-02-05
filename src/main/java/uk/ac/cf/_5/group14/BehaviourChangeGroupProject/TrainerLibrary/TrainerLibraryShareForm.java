package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerLibrary;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TrainerLibraryShareForm {

    @NotNull
    private Long clientId;

    @NotNull
    private TrainerLibraryTemplateType templateType;

    @NotNull
    private Long templateId;

    @Size(max = 200)
    private String returnUrl;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public TrainerLibraryTemplateType getTemplateType() {
        return templateType;
    }

    public void setTemplateType(TrainerLibraryTemplateType templateType) {
        this.templateType = templateType;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
}
