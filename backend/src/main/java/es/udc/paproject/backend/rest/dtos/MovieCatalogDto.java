package es.udc.paproject.backend.rest.dtos;

public class MovieCatalogDto {
    private Long id;
    private String title;
    private int duration;
    private String genre;

    public MovieCatalogDto() {}

    public MovieCatalogDto(Long id, String title, int duration, String posterUrl) {
        this.id = id;
        this.title = title;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}