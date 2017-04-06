package com.secunet.eidserver.testbed.runner;

import java.util.HashSet;
import java.util.Iterator;

public class KnownValues extends HashSet<KnownValue> {
	private static final long serialVersionUID = -4974866043971398158L;
	
	@Override
	public boolean add(KnownValue knownValue) {
		KnownValue val = null;
		if((val = get(knownValue.getName())) !=null) {
			this.remove(val);
		}
		return super.add(knownValue);
	}

	/**
	 * Returns the {@link KnownValue} with the given name, or <i>null</i> if no element with the given value is found 
	 * @param name
	 * @return
	 */
	public KnownValue get(String name) {
		if(name == null) {
			return null;
		}
		Iterator<KnownValue> iter = this.iterator();
		while(iter.hasNext()) {
			KnownValue value = iter.next();
			if(value.getName().equalsIgnoreCase(name)) {
				return value; 
			}
		}
		return null;
	}
	
	/**
	 * Returns a subset of this set containing all {@link KnownValue} beginning with the given prefix 
	 * @param name
	 * @return
	 */
	public KnownValues getStartingWith(String prefix) {
		if(prefix == null) {
			return null;
		}
		KnownValues values = new KnownValues();
		Iterator<KnownValue> iter = this.iterator();
		while(iter.hasNext()) {
			KnownValue value = iter.next();
			if(value.getName().startsWith(prefix)) {
				values.add(value);
			}
		}
		return values;
	}
	
	/**
	 * Check whether the set contains a {@link KnownValue} with the given name
	 * @param name
	 * @return
	 */
	public boolean containsElement(String name) {
		if(name == null) {
			return false;
		}
		Iterator<KnownValue> iter = this.iterator();
		while(iter.hasNext()) {
			if(iter.next().getName().equalsIgnoreCase(name)) {
				return true; 
			}
		}
		return false;
	}

}
