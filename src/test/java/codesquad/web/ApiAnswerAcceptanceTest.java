package codesquad.web;

import codesquad.dto.AnswerDto;
import org.junit.Test;
import support.test.AcceptanceTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final String URL_BASE = "/api/questions/1/answers";
    private static final AnswerDto NEW_ANSWER = new AnswerDto(3,"Test Answer");

    @Test
    public void create() throws Exception {
        String location = create(URL_BASE, NEW_ANSWER);

        AnswerDto dbAnswer = template().getForObject(location, AnswerDto.class);
        assertThat(dbAnswer, is(NEW_ANSWER));
    }
}
