#ifndef VECTOR_HEADER
#define VECTOR_HEADER

#include "collections.h"

using std;
in core in collections {


class Vector : Collection {
	private Object* backing;
	private size_t len;
	private size_t capacity;

	public Vector();
	public Vector(size_t capacity);

	public Object get(size_t index);
	public bool remove(size_t index);
	public void add(Object o);
};


}

#endif