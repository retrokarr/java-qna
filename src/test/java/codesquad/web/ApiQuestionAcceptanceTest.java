package codesquad.web;

import codesquad.dto.QuestionDto;
import com.sun.deploy.net.HttpResponse;
import org.junit.Test;
import org.springframework.cache.support.NullValue;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import javax.xml.ws.Response;

import static codesquad.domain.UserTest.newUser;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;


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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = put("/api/questions/1", UPDATED_QUESTION);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        QuestionDto dbQuestion = template().getForObject("/api/questions/1", QuestionDto.class);
        assertThat(dbQuestion, is(UPDATED_QUESTION));
    }

    @Test
    public void update_by_not_owner() throws Exception {
        ResponseEntity<String> response = put("/api/questions/2", UPDATED_QUESTION);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));

        QuestionDto dbQuestion = template().getForObject("/api/questions/2", QuestionDto.class);
        assertThat(dbQuestion, not(UPDATED_QUESTION));
    }

    @Test
    public void delete() throws Exception {
        basicAuthTemplate().delete("/api/questions/1");

        QuestionDto dbQuestion = template().getForObject("/api/questions/1", QuestionDto.class);
        assertThat(dbQuestion, is(nullValue()));
    }
}
