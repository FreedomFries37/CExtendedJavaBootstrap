#ifndef COLLECTION_HEADER
#define COLLECTION_HEADER

#include "iterator.h"

in core in collections {

class Collection {
	virtual public int len();
	virtual public Iterator toIterator();
};


}

#endif