package es.udc.paproject.backend.rest.dtos;

public class MovieDetailsDto {
    private Long id;
    private String title;
    private String summary;
    private int duration;

    public MovieDetailsDto() {}

    public MovieDetailsDto(Long id, String title, String summary, int duration) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}