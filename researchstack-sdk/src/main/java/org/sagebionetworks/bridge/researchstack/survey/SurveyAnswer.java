package org.sagebionetworks.bridge.researchstack.survey;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.utils.FormatHelper;

import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class SurveyAnswer {
    @NonNull
    public final int questionType;
    @NonNull
    public final String questionTypeName;
    @NonNull
    public final String item;
    @NonNull
    public final String startDate;
    @NonNull
    public final String endDate;

    private SurveyAnswer(@NonNull StepResult stepResult) {
        checkNotNull(stepResult);

        AnswerFormat.Type type = (AnswerFormat.Type) stepResult.getAnswerFormat().getQuestionType();
        this.questionType = type.ordinal();
        checkNotNull(this.questionType);

        this.questionTypeName = type.name();
        checkNotNull(this.questionTypeName);

        this.startDate = FormatHelper.DEFAULT_FORMAT.format(stepResult.getStartDate());
        checkNotNull(this.startDate);

        this.item = stepResult.getIdentifier();
        checkNotNull(this.item);

        this.endDate = FormatHelper.DEFAULT_FORMAT.format(stepResult.getEndDate());
        checkNotNull(this.endDate);
    }

    @NonNull
    public static SurveyAnswer create(@NonNull StepResult stepResult) {
        checkNotNull(stepResult);

        AnswerFormat.Type type = (AnswerFormat.Type) stepResult.getAnswerFormat().getQuestionType();
        SurveyAnswer answer;
        switch (type) {
            case SingleChoice:
            case MultipleChoice:
                answer = new ChoiceSurveyAnswer(stepResult);
                break;
            case Integer:
                answer = new NumericSurveyAnswer(stepResult);
                break;
            case Boolean:
                answer = new BooleanSurveyAnswer(stepResult);
                break;
            case Text:
                answer = new TextSurveyAnswer(stepResult);
                break;
            case Date:
                answer = new DateSurveyAnswer(stepResult);
                break;
            case ImageChoice:
                if (stepResult.getResult() instanceof Number) {
                    answer = new NumericSurveyAnswer(stepResult);
                } else if (stepResult.getResult() instanceof String) {
                    answer = new TextSurveyAnswer(stepResult);
                } else {
                    throw new RuntimeException("Cannot upload ImageChoice result to bridge");
                }
                break;
            case None:
            case Scale:
            case Decimal:
            case Eligibility:
            case TimeOfDay:
            case DateAndTime:
            case TimeInterval:
            case Location:
            case Form:
            default:
                throw new RuntimeException("Cannot upload this question type to bridge");
        }
        return answer;
    }

    public static class BooleanSurveyAnswer extends SurveyAnswer {

        private final Boolean booleanAnswer;

        public BooleanSurveyAnswer(StepResult result) {
            super(result);
            booleanAnswer = (Boolean) result.getResult();
        }
    }

    public static class ChoiceSurveyAnswer extends SurveyAnswer {
        public static final String KEY_CHOICE_ANSWERS = "choiceAnswers";

        @NonNull
        @SerializedName(KEY_CHOICE_ANSWERS)
        private final List<?> choiceAnswers;

        public ChoiceSurveyAnswer(@NonNull StepResult stepResult) {
            super(stepResult);

            Object result = stepResult.getResult();
            if (result instanceof List) {
                choiceAnswers = ImmutableList.copyOf((List<?>) result);
            } else {
                choiceAnswers = ImmutableList.of(result);
            }
        }
    }

    public static class NumericSurveyAnswer extends SurveyAnswer {

        private final Integer numericAnswer;

        public NumericSurveyAnswer(StepResult result) {
            super(result);
            numericAnswer = (Integer) result.getResult();
        }
    }

    public static class TextSurveyAnswer extends SurveyAnswer {

        private final String textAnswer;

        public TextSurveyAnswer(StepResult result) {
            super(result);
            textAnswer = (String) result.getResult();
        }
    }

    public static class DateSurveyAnswer extends SurveyAnswer {

        private final String dateAnswer;

        public DateSurveyAnswer(StepResult result) {
            super(result);
            Long dateResult = (Long) result.getResult();
            dateAnswer =
                    dateResult == null ? null : FormatHelper.DEFAULT_FORMAT.format(new Date(dateResult));
        }
    }
}
