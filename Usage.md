_Describe how to run the various predictors algorithms_

# Introduction #

In this wiki we'll explain in details how to configure and run the predictors implemented in the project.

# Configuration #

Different parameters used by the predictors can be configured using the [predictors.properties](http://code.google.com/p/oknetflix/source/browse/trunk/src/main/resources/predictors.properties) file located in the resources folder.
Every predictor has its own parameters marked by the predictor name as a prefix follow by the parameter name. For example, in order to configure the number of neighbors used by the KNN predictor, you should edit the "knn.neighborsNum" property.
For a full description regarding every property, please see the documentation inside the file.

# Running the predictors #

**Remark:** All classes mentioned below should be run with heap size set to at least 3000m (we recommend setting it to 3300m).

Before running the predictors it's advised to run the [AlgorithmRunner](http://code.google.com/p/oknetflix/source/browse/trunk/src/main/java/edu/mta/ok/nworkshop/ui/AlgorithmRunner.java) class and push the "Run Global Effects Calculator" button in order to calculate the models containing residuals of global effects as rating data.

  * ## _KNN Predictor_ ##
    1. Calculate similarity model:
```
SimilarityCalculator simCalc = new PearsonCorrelationCoefifcientSimilarity();		

simCalc.calculateSimilarities();		

simCalc.saveCalculatedData(Constants.NETFLIX_OUTPUT_DIR + "residualsSimilarityModelScores.data");
```
    1. Run the predictor class:
```
KNNGlobalEffectPredictor predictorClass = new KNNGlobalEffectPredictor(PearsonCorrelationCoefifcientSimilarity.getSimilarityFromFile(Constants.NETFLIX_OUTPUT_DIR + "\\residualsSimilarityModelScores.data"), 
				new UserIndexedModelResiduals(), PredictorProperties.getInstance().getPredictorIntProperty(Predictors.KNN, PropertyKeys.NEIGHBORS_NUM, 50), Constants.NETFLIX_OUTPUT_DIR + "globalEffects/Effect11.data");
PredictionTester.getProbeError(predictorClass, Constants.NETFLIX_OUTPUT_DIR + "Predictions/KNN.txt", Constants.NETFLIX_OUTPUT_DIR + Constants.DEFAULT_PROBE_FILE_NAME);
```

> _Remark:_ In the current section we explained how to run the KNN algorithm using residuals of global effects as ratings data, in order to run the algorithm using the raw ratings scores you'll need to run the appropriate similarity and predictor classes (for more information see [KNNPredictor](http://code.google.com/p/oknetflix/source/browse/trunk/src/main/java/edu/mta/ok/nworkshop/predictor/KNNPredictor.java) and [PearsonCorrelationCoefifcientSimilarityRawScores](http://code.google.com/p/oknetflix/source/browse/trunk/src/main/java/edu/mta/ok/nworkshop/similarity/PearsonCorrelationCoefifcientSimilarityRawScores.java).

  * ## _Improved KNN Predictor_ ##
    1. Calculate the common raters model:
```
ModelUtils.calculateCommonRatersNum(new MovieIndexedModelRatings(), new UserIndexedModelRatings());
```
    1. Calculate the interpolation values by running the PreProcessItemViewUsers class:
```
PreProcessItemViewUsers processClass = new PreProcessItemViewUsers(Constants.NETFLIX_OUTPUT_DIR + "cleanedMovieIndexedSlabDates.data", Constants.NETFLIX_OUTPUT_DIR + "cleanedUserIndexedSlabDates.data");
processClass.calcFinalValues();
```
    1. Calculate interpolation similarity model:
```
InterpolationSimilarityResiduals sim = new InterpolationSimilarityResiduals(Constants.NETFLIX_OUTPUT_DIR + "cleanedMovieIndexedSlabDates.data", Constants.NETFLIX_OUTPUT_DIR + "cleanedUserIndexedSlabDates.data");
sim.calculateSimilarities();
sim.saveCalculatedData(Constants.NETFLIX_OUTPUT_DIR + "interpolation\\similarityModel.data");
```
    1. Convert the similarity model to float in order to decrease memory consumption:
```
ModelUtils.convertDoubleModelIntoFloat(Constants.NETFLIX_OUTPUT_DIR + "interpolation\\similarityModel.data", Constants.NETFLIX_OUTPUT_DIR + "interpolation\\similarityModel-Float.data");
```
    1. Run the improved knn predictor:
```
ImprovedKNNPredictionResiduals predictor = new ImprovedKNNPredictionResiduals(
				InterpolationSimilarityRawScores.getSimilarityFromFile(Constants.NETFLIX_OUTPUT_DIR + "interpolation\\similarityModel-Float.data", true),new UserIndexedModelResiduals(), Constants.NETFLIX_OUTPUT_DIR + "interpolation\\moviesCommonUsersLists-Final.data");
	
PredictionTester.getProbeError(predictor, Constants.NETFLIX_OUTPUT_DIR + "Predictions/InterpolationPredictorResiduals-" + DEFAULT_NEIGHBOARS_NUM +"Neighbors.txt", Constants.NETFLIX_OUTPUT_DIR + Constants.DEFAULT_PROBE_FILE_NAME);
```
  * ## _SVD_ ##
> > Run Predictor (the number of features is taken from predictors,properties file):
```
SVDFeaturePredictor predictor = new SVDFeaturePredictor();
PredictionTester.getProbeError(predictor, Constants.NETFLIX_OUTPUT_DIR + "Predictions/SVD.txt", Constants.NETFLIX_OUTPUT_DIR + Constants.DEFAULT_PROBE_FILE_NAME);
```
  * ## _Improved SVD_ ##
> > Run predictor (the number of features is taken from predictors,properties file):
```
ImprovedSVDFeaturePredictor predictor = new ImprovedSVDFeaturePredictor();
PredictionTester.getProbeError(predictor, Constants.NETFLIX_OUTPUT_DIR + "Predictions/ImprovedSVD.txt", Constants.NETFLIX_OUTPUT_DIR + Constants.DEFAULT_PROBE_FILE_NAME);
```
  * ## _KNN-SVD_ ##
> > _Prerequisite: The following example assumes that there is a pre calculated SVD model created from SVD/Improved SVD algorithm. The file name is SVD-256-features.data located under SVD folder in the output files folder._
> > Run predictor:
```
KNNSVDPredictor predictor = new KNNSVDPredictor(SVDFeaturePredictor.getPredictor(Constants.NETFLIX_OUTPUT_DIR + "SVD/SVD-256-features.data"));
predictor.setUserModel(new UserIndexedModelRatings());
PredictionTester.getProbeError(predictor, Constants.NETFLIX_OUTPUT_DIR + "Predictions/KNNRegularSVD.txt", Constants.NETFLIX_OUTPUT_DIR + Constants.DEFAULT_PROBE_FILE_NAME); 
```
  * ## _Combine Predictions_ ##
> > After calculating the predictions files from different algorithms, you can combine the results by running the [AlgorithmRunner](http://code.google.com/p/oknetflix/source/browse/trunk/src/main/java/edu/mta/ok/nworkshop/ui/AlgorithmRunner.java) class and pressing the "Combine Predictions" button. The button simply loads all the text files located in binFiles/Predictions and calls [CombinePredictions](http://code.google.com/p/oknetflix/source/browse/trunk/src/main/java/edu/mta/ok/nworkshop/predictor/CombinePredictions.java) with the loaded predictions.

**REMARK:** All predictors support loading pre calculated data from binary files in order to reduce running time. For more information see the main method inside the predictors classes.

# Achieving 0.8988 RMSE Score #

We achieved our RMSE score by combining results from the following algorithms:

  * KNN:
    1. RMSE=1.0460. k=20 using raw ratings data.
    1. RMSE=1.0515. k=30 using raw ratings data.
    1. RMSE=1.0621. k=50 using raw ratings data.
    1. RMSE=0.9311. k=20 using residuals of all 11 global effects.
    1. RMSE=0.9299. k=30 using residuals of all 11 global effects.
    1. RMSE=0.9308. k=50 using residuals of all 11 global effects.
  * Improved KNN (Bellkor interpolation):
    1. RMSE=0.9285. k=20 using residuals of all 11 global effects.
    1. RMSE=0.9260. k=30 using residuals of all 11 global effects.
    1. RMSE=0.9241. k=50 using residuals of all 11 global effects.
  * SVD:
    1. RMSE=0.9235. Factors=10 using raw ratings data.
    1. RMSE=0.9079. Factors=96 using raw ratings data.
    1. RMSE=0.9058. Factors=256 using raw ratings data.
  * Improved SVD:
    1. RMSE=0.9228. Factors=10 using raw ratings data.
    1. RMSE=0.9078. Factors=96 using raw ratings data.
    1. RMSE=0.9055. Factors=256 using raw ratings data.
  * KNN-SVD:
    1. RMSE=1.2319. Factors=256, k=1 using raw ratings data.
  * Global effects:
    1. RMSE=0.9658. using all 11 global effects.