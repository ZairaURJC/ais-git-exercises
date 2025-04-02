package es.codeurjc.web.nitflex.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import es.codeurjc.web.nitflex.dto.film.*;
import es.codeurjc.web.nitflex.repository.FilmRepository;
import es.codeurjc.web.nitflex.repository.UserRepository;
import es.codeurjc.web.nitflex.service.FilmService;
import es.codeurjc.web.nitflex.service.exceptions.FilmNotFoundException;
import es.codeurjc.web.nitflex.utils.ImageUtils;

class FilmServiceTest {

    private FilmRepository filmRepository;
    private UserRepository userRepository;
    private ImageUtils imageUtils;
    private FilmMapper filmMapper;
    private FilmService filmService;

    @BeforeEach
    void setup(){
        filmRepository=mock(FilmRepository.class);
        userRepository=mock(UserRepository.class);
        imageUtils=mock(ImageUtils.class);
        filmMapper=Mappers.getMapper(FilmMapper.class);
        filmService=new FilmService(filmRepository, userRepository, imageUtils, filmMapper);
    }
    
    @Test
    void save_film_without_image_and_with_valid_title(){
        CreateFilmRequest film=new CreateFilmRequest("validTitle", null, 0, null);

        filmService.save(film);

        verify(filmRepository).save(filmMapper.toDomain(film));
    }

    @Test
    void delete_non_existant_film(){
        Long id=(long) 5;
        when(filmRepository.findById(id)).thenThrow(new FilmNotFoundException(id));

        FilmNotFoundException ex=assertThrows(FilmNotFoundException.class, ()->filmService.delete(id));

        verify(filmRepository,never()).deleteById(id);
        assertEquals("Film not found with id: 5",ex.getMessage());
    }
}
