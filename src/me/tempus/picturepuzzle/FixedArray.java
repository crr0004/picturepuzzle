package me.tempus.picturepuzzle;


/**
 * 
 */

/**
 * @author Chris
 *
 */
public class FixedArray<T> {

	private static final int DEFAULT_SIZE = 64;
	private T[] contents;
	public int contentIndex = 0;
	private int[] removedIndincies;
	private int removedIndinciesIndex = 0;
	private boolean contentsHasVacancy = false;
	
	@SuppressWarnings("unchecked")
	public FixedArray(int size){
		contents = ((T[]) new Object[size]);
		removedIndincies = new int[size/2];
	}
	
	@SuppressWarnings("unchecked")
	public FixedArray(){
		contents = ((T[]) new Object[DEFAULT_SIZE]);
		removedIndincies = new int[DEFAULT_SIZE/2];
	}
	
	/**
	 * Adds an object to the array
	 * @param object Object to be added to the array
	 * @return The index of the object in the array, -1 if it wasn't added
	 */
	public int add(T object){
			if(contentsHasVacancy){
				final int index = removedIndincies[removedIndinciesIndex-1];
				removedIndincies[removedIndinciesIndex] = -1;
				removedIndinciesIndex--;
				if(contents[index] != null)
					System.err.println("Overriding object in array!");
				contents[index] = object;
				if(removedIndinciesIndex < 1){
					contentsHasVacancy = false;
				}
				return index;
			}else if(contentIndex < contents.length){				
				contents[contentIndex] = object;
				contentIndex++;
				return contentIndex-1;
			}
		return -1;
	}
	
	/**
	 * Removes an element out of the array
	 * Override tells it not to buffer the position to the vacant slots
	 * @param index
	 * @param override True if prevent buffer position to vacant slots
	 */
	public void remove(int index, boolean override){
		if(index < contents.length){
			
			contents[index] = null;
			if(!override){
				contentsHasVacancy = true;
				removedIndincies[removedIndinciesIndex] = index;
				removedIndinciesIndex++;
			}
		}
	}
	
	/**
	 * Be careful, any data changed in the array will reflect in the contents here. This should only be used in a read-only instance.
	 * @return contents
	 */
	public Object[] getContents(){
		return contents;
	}
	
	@SuppressWarnings("unchecked")
	public void fill(){
		for(int i = 0; i < contents.length; i++){
			contents[i] = (T) new Object();
			contentIndex++;
		}
	}
	
	public void reset() {
		contentIndex = 0;
		contentsHasVacancy = false;
		removedIndinciesIndex = 0;
	}

}
