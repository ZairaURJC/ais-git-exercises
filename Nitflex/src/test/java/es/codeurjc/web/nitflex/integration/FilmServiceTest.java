package es.codeurjc.web.nitflex.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Blob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import es.codeurjc.web.nitflex.dto.film.CreateFilmRequest;
import es.codeurjc.web.nitflex.dto.film.FilmDTO;
import es.codeurjc.web.nitflex.dto.film.FilmMapper;
import es.codeurjc.web.nitflex.dto.film.FilmSimpleDTO;
import es.codeurjc.web.nitflex.model.Film;
import es.codeurjc.web.nitflex.repository.FilmRepository;
import es.codeurjc.web.nitflex.repository.UserRepository;
import es.codeurjc.web.nitflex.service.FilmService;
import es.codeurjc.web.nitflex.utils.ImageUtils;

@SpringBootTest
class FilmServiceTest {

    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageUtils imageUtils;
    private FilmMapper filmMapper;
    private FilmService filmService;
    private CreateFilmRequest film;

    @BeforeEach
    void setup(){
        filmMapper=Mappers.getMapper(FilmMapper.class);
        filmService=new FilmService(filmRepository, userRepository, imageUtils, filmMapper);
        film=new CreateFilmRequest("validTitle", null, 0, null);
    }

    @Test
    void save_film_without_image_and_with_valid_title(){
        FilmDTO returnFilm=filmService.save(film);
        Film repFilm=filmRepository.findById(returnFilm.id()).get();

        assertEquals("validTitle", returnFilm.title());
        assertEquals("validTitle",repFilm.getTitle());
    }

    @Test
    void update_title_and_synopsis_of_film_with_image(){
        Blob filmImage=imageUtils.localImageToBlob("Nitflex/images/deadpool.jpg");
        FilmDTO returnFilm=filmService.save(film, filmImage);
        Film toUpdateFilm=new Film("newTitle","newSynopsis",0,null);
        FilmSimpleDTO toUpdateFilmSimpleDTO=filmMapper.toFilmSimpleDTO(toUpdateFilm);

        filmService.update(returnFilm.id(), toUpdateFilmSimpleDTO);
        Film updatedFilm=filmRepository.findById(returnFilm.id()).get();

        assertEquals("newTitle",updatedFilm.getTitle());
        assertEquals("newSynopsis",updatedFilm.getSynopsis());
        assertEquals(filmImage,updatedFilm.getPosterFile());
    }
}
