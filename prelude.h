in std class ClassInfo;
in std class String;

typedef unsigned long size_t;
void* malloc(size_t sz);
void free(void* ptr);

void print(char* o);
void println(char* o);
void print(std::String o);
void println(std::String o);

in std {
	[setAsDefaultInheritance]
	class Object { // default inheritence is initially null

		private ClassInfo info;
		private long references; // if using garbage collection

		public Object();

		virtual public int hashcode() {
			return 1;
		}
		virtual public int equals(Object other) {
			return 1;
		}
		virtual public void drop() {

		}
		public ClassInfo getClassInfo() {
			return this->info;
		}
	};

	class ClassInfo {
		private String name;
		private ClassInfo parent;
		private int classHash;

		private ClassInfo(String name, ClassInfo parent, int classHash);
	};

	class String {
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
			super->drop();
			free(this->backingPtr);
		}

		public String concat(char* other) {
			return this->concat(new String(other));
		}

		public String concat(String other) {
			char* next = (char*) malloc(sizeof(char) * (this->length + other->length + 1));
			for(int i = 0; i < this->length; i++) {
				next[i] = (char) this->backingPtr[i];
			}

			for(int i = 0; i < other->length; i++) {
				next[i + other->length] = (char) other->backingPtr[i];
			}

			return new String(next);
		}


		public const char* getCStr() {
			return this->backingPtr;
		}
    };

}