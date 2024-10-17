package com.certidevs;

import com.certidevs.model.Film;
import com.certidevs.repository.FilmRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {

		var context = SpringApplication.run(Main.class, args);

		FilmRepository filmRepository = context.getBean(FilmRepository.class);

		long filmNumber = filmRepository.count();
		if (filmNumber > 0)
			return;

		var film1 = Film.builder().title("The Godfather").director("Francis Ford Coppola").year(1972L).rented(false).build();
		var film2 = Film.builder().title("Back to the Future").director("Robert Zemeckis").year(1985L).rented(true).build();

		filmRepository.save(film1);
		filmRepository.save(film2);


	}

}
