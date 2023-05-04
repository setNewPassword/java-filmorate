package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AppError {
    private String message;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime time;

    public void setData(String message, String description) {
        this.message = message != null ? message : "";
        this.description = description != null ? description : "";
        this.time = LocalDateTime.now();
    }
}