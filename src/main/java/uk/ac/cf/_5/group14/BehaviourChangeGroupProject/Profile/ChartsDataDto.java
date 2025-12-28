package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChartsDataDto {
    private List<String> averageMoodLabels;
    private List<Double> averageMoodData;

    private List<String> averageConfidenceLabels;
    private List<Double> averageConfidenceData;

    private List<String> bmiLabels;
    private List<Double> bmiData;

    private List<String> waistHeightRatioLabels;
    private List<Double> waistHeightRatioData;

    private List<String> cholesterolLabels;
    private List<Double> cholesterolData;


    private List<String> bloodPressureLabels;
    private List<Integer> systolicBPData;
    private List<Integer> diastolicBPData;


}
