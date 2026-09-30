package org.json.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Stores trimmed string items parsed from delimiter-separated text and can
 * join them back into a delimited representation.
 */
public class ItemList {
	private String sp=",";
	List items=new ArrayList();
	
	
	/** Creates an empty list using a comma as its default delimiter. */
	public ItemList(){}
	
	
	/**
	 * Parses text into items separated by commas.
	 *
	 * @param s text to split
	 */
	public ItemList(String s){
		this.split(s,sp,items);
	}
	
	/**
	 * Parses text using the supplied delimiter.
	 *
	 * @param s text to split
	 * @param sp delimiter between items
	 */
	public ItemList(String s,String sp){
		this.sp=sp;
		this.split(s,sp,items);
	}
	
	/**
	 * Parses text using either literal-delimiter or delimiter-character-set
	 * splitting.
	 *
	 * @param s text to split
	 * @param sp delimiter, or set of delimiter characters when multi-token mode
	 *           is enabled
	 * @param isMultiToken whether each character in {@code sp} separates items
	 */
	public ItemList(String s,String sp,boolean isMultiToken){
		split(s,sp,items,isMultiToken);
	}
	
	/**
	 * Returns the mutable list that stores this object's items.
	 *
	 * @return backing list of trimmed item strings
	 */
	public List getItems(){
		return this.items;
	}
	
	/**
	 * Returns the stored items as an array.
	 *
	 * @return array containing the items in list order
	 */
	public String[] getArray(){
		return (String[])this.items.toArray();
	}
	
	/**
	 * Splits text and appends its trimmed parts to a destination list. In
	 * multi-token mode, each character in the delimiter string is a separator.
	 *
	 * @param s text to split
	 * @param sp literal delimiter or delimiter-character set
	 * @param append destination receiving the parts
	 * @param isMultiToken whether to treat delimiter characters individually
	 */
	public void split(String s,String sp,List append,boolean isMultiToken){
		if(s==null || sp==null)
			return;
		if(isMultiToken){
			StringTokenizer tokens=new StringTokenizer(s,sp);
			while(tokens.hasMoreTokens()){
				append.add(tokens.nextToken().trim());
			}
		}
		else{
			this.split(s,sp,append);
		}
	}
	
	/**
	 * Splits text at each occurrence of a literal delimiter and appends trimmed
	 * parts to the destination list.
	 *
	 * @param s text to split
	 * @param sp literal delimiter
	 * @param append destination receiving the parts
	 */
	public void split(String s,String sp,List append){
		if(s==null || sp==null)
			return;
		int pos=0;
		int prevPos=0;
		do{
			prevPos=pos;
			pos=s.indexOf(sp,pos);
			if(pos==-1)
				break;
			append.add(s.substring(prevPos,pos).trim());
			pos+=sp.length();
		}while(pos!=-1);
		append.add(s.substring(prevPos).trim());
	}
	
	/**
	 * Sets the delimiter used by default parsing and rendering operations.
	 *
	 * @param sp delimiter value
	 */
	public void setSP(String sp){
		this.sp=sp;
	}
	
	/**
	 * Inserts a non-null item at the specified position after trimming it.
	 *
	 * @param i insertion index
	 * @param item item text; {@code null} is ignored
	 */
	public void add(int i,String item){
		if(item==null)
			return;
		items.add(i,item.trim());
	}

	/**
	 * Appends a non-null, trimmed item.
	 *
	 * @param item item text; {@code null} is ignored
	 */
	public void add(String item){
		if(item==null)
			return;
		items.add(item.trim());
	}
	
	/**
	 * Appends all items from another list in their existing order.
	 *
	 * @param list source of items to append
	 */
	public void addAll(ItemList list){
		items.addAll(list.items);
	}
	
	/**
	 * Splits text using this list's configured delimiter and appends the parts.
	 *
	 * @param s text to split
	 */
	public void addAll(String s){
		this.split(s,sp,items);
	}
	
	/**
	 * Splits text using a literal delimiter and appends the parts.
	 *
	 * @param s text to split
	 * @param sp literal delimiter
	 */
	public void addAll(String s,String sp){
		this.split(s,sp,items);
	}
	
	/**
	 * Splits text using the selected delimiter mode and appends the parts.
	 *
	 * @param s text to split
	 * @param sp literal delimiter or delimiter-character set
	 * @param isMultiToken whether to treat delimiter characters individually
	 */
	public void addAll(String s,String sp,boolean isMultiToken){
		this.split(s,sp,items,isMultiToken);
	}
	
	/**
	 * Returns the item at a zero-based position.
	 *
	 * @param i item index
	 * @return item at {@code i}
	 * @throws IndexOutOfBoundsException if the index is outside the list
	 */
	public String get(int i){
		return (String)items.get(i);
	}
	
	/**
	 * Returns the number of stored items.
	 *
	 * @return item count
	 */
	public int size(){
		return items.size();
	}

	/**
	 * Joins the items using this list's configured delimiter.
	 *
	 * @return joined item text
	 */
	public String toString(){
		return toString(sp);
	}
	
	/**
	 * Joins the items using a caller-supplied delimiter.
	 *
	 * @param sp delimiter inserted between adjacent items
	 * @return joined item text
	 */
	public String toString(String sp){
		StringBuffer sb=new StringBuffer();
		
		for(int i=0;i<items.size();i++){
			if(i==0)
				sb.append(items.get(i));
			else{
				sb.append(sp);
				sb.append(items.get(i));
			}
		}
		return sb.toString();

	}
	
	/** Removes all items while retaining the configured delimiter. */
	public void clear(){
		items.clear();
	}
	
	/** Restores the comma delimiter and removes all items. */
	public void reset(){
		sp=",";
		items.clear();
	}
}
