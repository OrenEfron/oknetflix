package edu.mta.ok.nworkshop.model;

import edu.mta.ok.nworkshop.PredictorProperties;
import edu.mta.ok.nworkshop.utils.ModelUtils;

/**
 * A class that holds a movie indexed model with movie IDs and raw rating scores.
 * Ratings are held in a two dimensional byte type array.
 */
public class MovieIndexedModelRatings implements MovieIndexedModel {

	protected int[][] userIds;
	protected byte[][] ratings;
	private ModelSorter sorter = new ModelSorter();
	
	public MovieIndexedModelRatings(){
		this(PredictorProperties.getInstance().getMovieIndexedModelFile());
	}
	
	public MovieIndexedModelRatings(String fileName) {
		super();
		
		loadModel(fileName);
	}
	
	/**
	 * Loads the movie indexed model from a given file
	 * 
	 * @param fileName the full path of the file we want to load the model from
	 */
	private void loadModel(String fileName){
		
		Object[] retVal = ModelUtils.loadMovieIndexedModel(fileName, false, false);
		
		userIds = (int[][])retVal[0];
		ratings = (byte[][])retVal[1];
	}

	@Override
	public Object[] getMovieData(short movieId) {
		return getMovieDataByIndex((short)(movieId - 1));
	}
	
	@Override
	public Object[] getMovieDataByIndex(short movieInd) {
		Object[] retVal = new Object[2];
		
		retVal[0] = userIds[movieInd];
		retVal[1] = ratings[movieInd];
		
		return retVal;
	}

	@Override
	public Object getMovieRatings(short movieId) {
		return getMovieRatingsByIndex((short)(movieId - 1));
	}
	
	@Override
	public Object getMovieRatingsByIndex(short movieInd) {
		return ratings[movieInd];
	}

	@Override
	public int[] getMovieRaters(short movieId) {
		return getMovieRatersByIndex((short)(movieId - 1));
	}
	
	@Override
	public void removeMovieDataByIndex(short movieInd) {
		if (movieInd < userIds.length && movieInd > 0){
			userIds[movieInd] = null;
			
			if (ratings != null && ratings[movieInd] != null){
				ratings[movieInd] = null;
			}			
		}
	}
	
	@Override
	public int[] getMovieRatersByIndex(short movieInd) {
		return userIds[movieInd];
	}

	@Override
	public void sortModel() {
		sorter.sortData();
	}
	
	@Override
	public Object[] getRatings() {
		return ratings;
	}
	
	@Override
	public int[][] getUserIds() {
		return userIds;
	}
	
	@Override
	public void removeRatings() {
		ratings = null;
		
	}



	/**
	 * Sorts the model according to the user ids in ascending order
	 */
	private class ModelSorter{

	    public void sortData(){
            if (userIds != null && userIds.length > 0){
            	
            	long start = System.currentTimeMillis();
            	
            	for (int i=0; i < userIds.length; i++){

            		if (userIds[i] != null && userIds[i].length > 0){
            			sort(i);
            		}
            		
            		if (i % 100 == 0 && i > 0){
            			System.out.println("Finished " + i + " movies. took: " + (System.currentTimeMillis() - start));
            			start = System.currentTimeMillis();
            		}
            	}
            }
	    }
	    
	    private void sort(int itemIndex){
	    	byte[] scoreTmpArray = new byte[ratings[itemIndex].length];
	    	int[] contentsTmpArray = new int[userIds[itemIndex].length];
	    	mergeSort(contentsTmpArray, scoreTmpArray, 0, ratings[itemIndex].length - 1, itemIndex);
	    }

	    public void mergeSort( int[] tempArrayContents, byte[] tempArrayScores, int left, int right, int itemIndex ) {

	        if( left < right ) {
	            int center = ( left + right ) / 2;
	            mergeSort( tempArrayContents, tempArrayScores, left, center, itemIndex );
	            mergeSort( tempArrayContents, tempArrayScores, center + 1, right, itemIndex );
	            merge( tempArrayContents, tempArrayScores, left, center + 1, right, itemIndex );
	        }
	    }
   
	    private void merge( int[] contentsArray, byte[] scoresArray, int leftPos, int rightPos, int rightEnd, int itemIndex ) {
	
	        int leftEnd = rightPos - 1;
	        int tmpPos = leftPos;
	        int numElements = rightEnd - leftPos + 1;
	
	        // Main loop
	        while( leftPos <= leftEnd && rightPos <= rightEnd ){
	            if( userIds[itemIndex][leftPos] < userIds[itemIndex][ rightPos ]){
	                scoresArray[tmpPos] = ratings[itemIndex][leftPos];
	                contentsArray[tmpPos++] = userIds[itemIndex][leftPos++];
	            }
	            else
	            {
	                scoresArray[tmpPos] = ratings[itemIndex][rightPos];
	                contentsArray[tmpPos++] = userIds[itemIndex][ rightPos++ ];
	            }
	        }
	
	        // Copy rest of first half
	        while( leftPos <= leftEnd ){
	            scoresArray[tmpPos] = ratings[itemIndex][leftPos];
	            contentsArray[tmpPos++] = userIds[itemIndex][ leftPos++ ];
	        }
	
	        // Copy rest of right half
	        while( rightPos <= rightEnd ){         
	            scoresArray[tmpPos] = ratings[itemIndex][rightPos];
	            contentsArray[tmpPos++] = userIds[itemIndex][rightPos++];
	        }
	
	        // Copy tmpArray back
	        for( int i = 0; i < numElements; i++, rightEnd-- ){       
	        	ratings[itemIndex][rightEnd] = scoresArray[rightEnd];
	        	userIds[itemIndex][rightEnd] = contentsArray[rightEnd];
	        }
	    }
	}
}
