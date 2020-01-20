in std class ClassInfo;
in std class String;


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
	[setAsDefaultInheritance]
	class Object { // default inheritence is initially null

		private ClassInfo info;
		public long references; // if using garbage collection

		public Object();

		virtual public int hashcode();
		virtual public int equals(Object other) {
			return this->hashcode() == other->hashcode();
		}
		virtual public void drop() {
			free(this);
		}



		virtual public String toString();

		public ClassInfo getClass() {
			return this->info;
		}


	};

	class ClassInfo {
		private String name;
		private ClassInfo parent;
		private int classHash;

		private ClassInfo(String name, ClassInfo parent, int classHash);

		public String getName() {
			return this->name;
		}

		public int is(Object o);

		virtual public int equals(Object other);
		virtual public int equals(ClassInfo other);
	};

	class String{
		char* backingPtr;
		int length;

		public String(const char* bp) {
			const char* ptr = bp;
			for(; *ptr != '\0'; ptr++) {
				++this->length;
			}
			this->backingPtr = malloc(sizeof(char) * (this->length + 1));
			this->backingPtr[this->length] = '\0';
			for(int i = 0; i < this->length; i++) {
				this->backingPtr[i] = (char) bp[i];
			}
		}

		public String() : this ("") { }

		virtual public void drop() {
			free(this->backingPtr);
			super->drop();
		}

		public String concat(String other) {
			char* next = (char*) malloc(sizeof(char) * (this->length + other->length + 1));
			for(int i = 0; i < this->length; i++) {
				next[i] = (char) this->backingPtr[i];
			}

			for(int i = 0; i < other->length; i++) {
				next[i + this->length] = (char) other->backingPtr[i];
			}

			next[this->length + other->length] = '\0';
			String output = new String(next);
			free(next);
			return output;
		}

		public String concat(char* other) {
			return this->concat(new String(other));
		}




		public const char* getCStr() {
			return this->backingPtr;
		}
    };

}