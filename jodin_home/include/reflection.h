#ifndef __REFLECTION__HEADER__
#define __REFLECTION__HEADER__

typedef unsigned long class_id;
in std {
	class ClassInfo __get_class(class_id id);
}
// Compiler has created a reflection.cx file


#endif