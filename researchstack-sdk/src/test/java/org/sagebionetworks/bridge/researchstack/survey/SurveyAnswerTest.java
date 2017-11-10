/*
 *    Copyright 2017 Sage Bionetworks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.sagebionetworks.bridge.researchstack.survey;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.junit.Test;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.sagebionetworks.bridge.rest.RestUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by liujoshua on 11/10/2017.
 */
public class SurveyAnswerTest {


    StepResult createStepResultMock(
            AnswerFormat answerFormat
    ) {
        StepResult mock = mock(StepResult.class);
        when(mock.getIdentifier()).thenReturn("id");
        when(mock.getAnswerFormat()).thenReturn(answerFormat);
        when(mock.getStartDate()).thenReturn(new Date());
        when(mock.getEndDate()).thenReturn(new Date());

        return mock;
    }

    <T extends SurveyAnswer> T createAnswer(StepResult result, Class<T> klass) {
        SurveyAnswer rawAnswer = SurveyAnswer.create(result);

        assertThat(rawAnswer, instanceOf(klass));
        return (T) rawAnswer;
    }

    @Test
    public void testJson_ChoiceSurveyAnswer() {
        StepResult stepResultMock = createStepResultMock(
                new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice)
        );

        List<String> result = Arrays.asList("Result1");
        when(stepResultMock.getResult()).thenReturn(result);

        SurveyAnswer.ChoiceSurveyAnswer answer = createAnswer(stepResultMock,
                SurveyAnswer.ChoiceSurveyAnswer.class);

        JsonObject node = RestUtils.GSON.toJsonTree(answer).getAsJsonObject();
        JsonArray array = node.getAsJsonArray(SurveyAnswer.ChoiceSurveyAnswer.KEY_CHOICE_ANSWERS);

        assertEquals(1, array.size());
        assertEquals(result.get(0), array.get(0).getAsString());
    }
}