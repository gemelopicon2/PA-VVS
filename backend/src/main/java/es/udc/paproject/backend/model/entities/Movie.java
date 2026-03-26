package es.udc.paproject.backend.model.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String summary;
    private int duration;

    public Movie(){}

    public Movie(String title, String summary, int duration){
        this.title = title;
        this.summary = summary;
        this.duration = duration;
    }

    public Long getId(){ return id;}
    public void setId(Long id){ this.id = id;}

    public String getTitle(){ return title;}
    public void setTitle(String title){ this.title = title;}

    public String getSummary(){ return summary;}
    public void setSummary(String summary){ this.summary = summary;}

    public int getDuration(){ return duration;}
    public void setDuration(int duration){ this.duration = duration;}
}
