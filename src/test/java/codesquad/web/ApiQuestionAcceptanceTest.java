package codesquad.web;

import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    public static final String URL_BASE = "/api/questions";
    public static final int QUESTION_IDX = 1;
    @Test
    public void create() throws Exception {
        QuestionDto newQuestion = new QuestionDto(3,"question", "contents");

        ResponseEntity<String> response = basicAuthTemplate().postForEntity(URL_BASE, newQuestion, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String location = response.getHeaders().getLocation().getPath();

        QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);
        assertThat(dbQuestion, is(newQuestion));
    }

    @Test
    public void update() throws Exception {
        QuestionDto updatedQuestion = new QuestionDto(1, "updated", "updated");

        basicAuthTemplate().put("/api/questions/1", updatedQuestion);

        QuestionDto dbQuestion = template().getForObject("/api/questions/1", QuestionDto.class);
        assertThat(dbQuestion, is(updatedQuestion));
    }
}
