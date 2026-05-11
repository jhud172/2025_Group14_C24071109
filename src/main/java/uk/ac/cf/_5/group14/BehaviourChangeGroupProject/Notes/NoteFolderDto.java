package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes;

public class NoteFolderDto {
    private Long id;
    private String name;
    private String colour;

    public static NoteFolderDto from(NoteFolder folder) {
        NoteFolderDto dto = new NoteFolderDto();
        dto.id = folder.getId();
        dto.name = folder.getName();
        dto.colour = folder.getColour();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColour() {
        return colour;
    }
}
