package codesquad.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.Size;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.hibernate.annotations.Where;

import codesquad.dto.QuestionDto;
import org.hibernate.hql.spi.id.persistent.DeleteHandlerImpl;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import static codesquad.domain.ContentType.QUESTION;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @Size(min = 3)
    @Lob
    private String contents;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public User getWriter() {
        return writer;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void update(User loginUser, Question updatedQuestion) {
        if(!this.isOwner(loginUser))
            throw new UnAuthorizedException();

        this.title = updatedQuestion.title;
        this.contents = updatedQuestion.contents;
    }

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        if(!this.isOwner(loginUser))
            throw new CannotDeleteException("Not owner");

        this.deleted = true;

        List<DeleteHistory> histories = this.deleteAnswers();
        histories.add(this.deleteHistory());

        return histories;
    }

    private DeleteHistory deleteHistory() {
        return new DeleteHistory(QUESTION, getId(), writer);
    }

    private boolean isAnswersDeletable() {
        return answers.stream()
                .allMatch(a -> a.isDeletable(writer));
    }

    private List<DeleteHistory> deleteAnswers() throws CannotDeleteException {
        if(!this.isAnswersDeletable())
            throw new CannotDeleteException("Answers can't be deleted");

        return answers.stream()
                .map(a -> a.delete(writer))
                .collect(Collectors.toList());
    }

    public QuestionDto toQuestionDto() {
        return new QuestionDto(getId(), this.title, this.contents);
    }

    public String generateApiUrl() {
        return "/api" + generateUrl();
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }
}
