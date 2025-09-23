package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FilmCommentControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmCommentController implements FilmCommentControllerApi {
    @Override
    public ResponseEntity<Integer> createComment(UpsertCommentDto dto) {
        log.info("Create comment for film id {}", dto.getFilmId());
        return ResponseEntity.ok(1);
    }

    @Override
    public ResponseEntity<Void> deleteComment(Integer commentId) {
        log.info("Delete comment by id {}", commentId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CommentDto> getComment(Integer commentId) {
        return ResponseEntity.ok(new CommentDto(322, OffsetDateTime.now(), 2, "Shrek", 2, "I was only nine years old. I loved Shrek so much, I had all the merchandise and movies. I'd pray to Shrek every night before I go to bed, thanking for the life I've been given. \"Shrek is love\", I would say, \"Shrek is life\". My dad hears me and calls me a faggot. I knew he was just jealous for my devotion of Shrek. I called him a cunt. He slaps me and sends me to go to sleep. I'm crying now and my face hurts. I lay in bed and it's really cold. A warmth is moving towards me. I feel something touch me. It's Shrek. I'm so happy. He whispers in my ear, \"This is my swamp\". He grabs me with his powerful ogre hands, and puts me on my hands and knees. I spread my ass-cheeks for Shrek. He penetrates my butthole. It hurts so much, but I do it for Shrek. I can feel my butt tearing as my eyes start to water. I push against his force. I want to please Shrek. He roars a mighty roar, as he fills my butt with his love. My dad walks in. Shrek looks him straight in the eye, and says, \"It's all ogre now\". Shrek leaves through my window. Shrek is love. Shrek is life.\uFEFF"));
    }

    @Override
    public ResponseEntity<CommentListDto> getCommentsByCriteria(CommentCriteriaDto criteria) {
        return ResponseEntity.ok(new CommentListDto(
                List.of(new CommentDto(14, OffsetDateTime.now(), 2, "Also Shrek", 2, """
                        ⢀⡴⠑⡄⠀⠀⠀⠀⠀⠀⠀⣀⣀⣤⣤⣤⣀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\s
                        ⠸⡇⠀⠿⡀⠀⠀⠀⣀⡴⢿⣿⣿⣿⣿⣿⣿⣿⣷⣦⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀\s
                        ⠀⠀⠀⠀⠑⢄⣠⠾⠁⣀⣄⡈⠙⣿⣿⣿⣿⣿⣿⣿⣿⣆⠀⠀⠀⠀⠀⠀⠀⠀\s
                        ⠀⠀⠀⠀⢀⡀⠁⠀⠀⠈⠙⠛⠂⠈⣿⣿⣿⣿⣿⠿⡿⢿⣆⠀⠀⠀⠀⠀⠀⠀\s
                        ⠀⠀⠀⢀⡾⣁⣀⠀⠴⠂⠙⣗⡀⠀⢻⣿⣿⠭⢤⣴⣦⣤⣹⠀⠀⠀⢀⢴⣶⣆\s
                        ⠀⠀⢀⣾⣿⣿⣿⣷⣮⣽⣾⣿⣥⣴⣿⣿⡿⢂⠔⢚⡿⢿⣿⣦⣴⣾⠁⠸⣼⡿\s
                        ⠀⢀⡞⠁⠙⠻⠿⠟⠉⠀⠛⢹⣿⣿⣿⣿⣿⣌⢤⣼⣿⣾⣿⡟⠉⠀⠀⠀⠀⠀\s
                        ⠀⣾⣷⣶⠇⠀⠀⣤⣄⣀⡀⠈⠻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡇⠀⠀⠀⠀⠀⠀\s
                        ⠀⠉⠈⠉⠀⠀⢦⡈⢻⣿⣿⣿⣶⣶⣶⣶⣤⣽⡹⣿⣿⣿⣿⡇⠀⠀⠀⠀⠀⠀\s
                        ⠀⠀⠀⠀⠀⠀⠀⠉⠲⣽⡻⢿⣿⣿⣿⣿⣿⣿⣷⣜⣿⣿⣿⡇⠀⠀⠀⠀⠀⠀\s
                        ⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⣷⣶⣮⣭⣽⣿⣿⣿⣿⣿⣿⣿⠀⠀⠀⠀⠀⠀⠀\s
                        ⠀⠀⠀⠀⠀⠀⣀⣀⣈⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠇⠀⠀⠀⠀⠀⠀⠀\s
                        ⠀⠀⠀⠀⠀⠀⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠃⠀⠀⠀⠀⠀⠀⠀⠀\s
                        ⠀⠀⠀⠀⠀⠀⠀⠹⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠟⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀\s
                        ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠛⠻⠿⠿⠿⠿⠛⠉
                        """)), 88L
        ));
    }

    @Override
    public ResponseEntity<Void> updateComment(Integer commentId, UpsertCommentDto dto) {
        log.info("Update comment by id {} for film id {}", commentId, dto.getFilmId());
        return ResponseEntity.noContent().build();
    }
}
