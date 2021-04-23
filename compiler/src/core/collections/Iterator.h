#ifndef __ITERATOR__HEADER__
#define __ITERATOR__HEADER__


in core in collections {
class Iterator {
	virtual public bool hasNext();
	virtual public std::Object next();
};
}


#define foreach(TYPE, VARIABLE, ITERATOR, BLOCK) while ((ITERATOR).hasNext()) { TYPE VARIABLE = (TYPE) (ITERATOR).next() BLOCK }

#endif
