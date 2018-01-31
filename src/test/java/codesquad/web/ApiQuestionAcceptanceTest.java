package codesquad.web;

import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static codesquad.domain.UserTest.newUser;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNot.not;


public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    public static final String URL_BASE = "/api/questions";
    public static final QuestionDto NEW_QUESTION = new QuestionDto(3, "question", "contents");
    public static final QuestionDto UPDATED_QUESTION = new QuestionDto(1, "updated", "updated");
    public static final int QUESTION_IDX = 1;

    @Test
    public void create() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate().postForEntity(URL_BASE, NEW_QUESTION, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String location = response.getHeaders().getLocation().getPath();

        QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);
        assertThat(dbQuestion, is(NEW_QUESTION));
    }

    @Test
    public void update() throws Exception {
        basicAuthTemplate().put("/api/questions/1", UPDATED_QUESTION);

        QuestionDto dbQuestion = template().getForObject("/api/questions/1", QuestionDto.class);
        assertThat(dbQuestion, is(UPDATED_QUESTION));
    }

    @Test
    public void update_by_not_owner() throws Exception {
        basicAuthTemplate(newUser("sanjigi")).put("/api/questions/1", UPDATED_QUESTION);

        QuestionDto dbQuestion = template().getForObject("/api/questions/1", QuestionDto.class);
        assertThat(dbQuestion, not(UPDATED_QUESTION));
    }

    public static QuestionDto
}
