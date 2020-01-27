#ifndef __PRELUDE_HEADER__
#define __PRELUDE_HEADER__

in std class ClassInfo;
in std class String;

typedef unsigned long class_id;
void* malloc(unsigned int sz);
void* calloc(unsigned int num, unsigned int sz);
void free(void* ptr);

void print(const char* c);
void println(const char* c);
void print_s(std::String o);
void println_s(std::String o);




/*
public <T> T inc(T o) {
	if((int) o == 0) return o;
	++o->references;
	return o;
}
`
public <T> T dec(T o) {
	if((int) o == 0) return o;
	--o->references;
	if(o->references <= 0) {
		o->drop();
		return (T) 0;
	}
	return o;
}
*/

in std {
	ClassInfo getClass(class_id id);

	[setAsDefaultInheritance]
	class Object { // default inheritence is initially null

		private ClassInfo info;
		public long references; // if using garbage collection

		public Object();

		virtual public int hashcode();
		virtual public int equals(Object other);
		virtual public void drop();



		virtual public String toString();

		public ClassInfo getClass();


	};

	class ClassInfo {
		private String name;
		private ClassInfo parent;
		private int classHash;

		private ClassInfo(String name, ClassInfo parent, int classHash);

		public String getName();

		public int is(Object o);

		virtual public int equals(Object other);
		virtual public int equals(ClassInfo other);
	};

	class String{
		char* backingPtr;
		int length;

		public String(const char* bp);

		public String();

		virtual public void drop();

		public String concat(String other);

		public String concat(char* other);

		public const char* getCStr();
    };

}

#endif