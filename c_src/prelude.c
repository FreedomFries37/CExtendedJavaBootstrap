typedef unsigned long int class_id;
struct class_std_ClassInfo215860611;
struct class_std_ClassInfo215860611* __get_class(class_id);
struct class_std_String708766606;
typedef unsigned char bool;
void* malloc(unsigned int);
void* calloc(unsigned int, unsigned int);
void free(void*);
void exit(int);
void panic(struct class_std_String708766606*);
void print(const char*);
void println(const char*);
void print_s(struct class_std_String708766606*);
void println_s(struct class_std_String708766606*);
static const void* nullptr = 0;
struct class_std_Object577384124;

struct class_std_Object577384124_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals1391760489) (void*, struct class_std_Object577384124*);
	void (*drop115478603) (void*);
	struct class_std_String708766606* (*toString1664535608) (void*);
};
struct class_std_Object577384124 {
	struct class_std_Object577384124_vtable* __vtable;
	struct class_std_ClassInfo215860611* info;
	long int references;
	struct class_std_ClassInfo215860611* (*getClass2062954782) (void*);
};

struct class_std_ClassInfo215860611* std_Object_getClass216694119(void*);
int std_Object_hashcode1585224288(void*);
bool std_Object_equals454500174(void*, struct class_std_Object577384124*);
void std_Object_drop1730782060(void*);
struct class_std_String708766606* std_Object_toString784171025(void*);

struct class_std_Object577384124* construct_std_Object0_317807437(void*);

static struct class_std_Object577384124* class_std_Object577384124_init1097101603() {
    struct class_std_Object577384124* output;
    output = calloc(1, sizeof(struct class_std_Object577384124));
    struct class_std_Object577384124_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_std_Object577384124_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1585224288;
    (*__vtable).equals1391760489 = std_Object_equals454500174;
    (*__vtable).drop115478603 = std_Object_drop1730782060;
    (*__vtable).toString1664535608 = std_Object_toString784171025;
    (*output).getClass2062954782 = std_Object_getClass216694119;
    (*output).info = (struct class_std_ClassInfo215860611*) __get_class(0);
    (*output).references = (long int) {0};
    return output;
}



struct class_std_ClassInfo215860611;

struct class_std_ClassInfo215860611_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals1391760489) (void*, struct class_std_Object577384124*);
	void (*drop115478603) (void*);
	struct class_std_String708766606* (*toString1664535608) (void*);
	bool (*equals1225076135) (void*, struct class_std_ClassInfo215860611*);
};
struct class_std_ClassInfo215860611 {
	struct class_std_ClassInfo215860611_vtable* __vtable;
	struct class_std_ClassInfo215860611* info;
	long int references;
	struct class_std_ClassInfo215860611* (*getClass2062954782) (void*);
	struct class_std_String708766606* name;
	struct class_std_ClassInfo215860611* parent;
	int classHash;
	struct class_std_String708766606* (*getName37078109) (void*);
	bool (*is_object2127914142) (void*, struct class_std_Object577384124*);
	bool (*is_class1664696309) (void*, struct class_std_ClassInfo215860611*);
};

struct class_std_String708766606* std_ClassInfo_getName493033082(void*);
bool std_ClassInfo_is_object1597802951(void*, struct class_std_Object577384124*);
bool std_ClassInfo_is_class2100159796(void*, struct class_std_ClassInfo215860611*);
int std_Object_hashcode1585224288(void*);
bool std_ClassInfo_equals861649298(void*, struct class_std_Object577384124*);
void std_Object_drop1730782060(void*);
struct class_std_String708766606* std_Object_toString784171025(void*);
bool std_ClassInfo_equals694964944(void*, struct class_std_ClassInfo215860611*);

struct class_std_ClassInfo215860611* construct_std_ClassInfo0_219382265(void*);

static struct class_std_ClassInfo215860611* class_std_ClassInfo215860611_init498330723() {
    struct class_std_ClassInfo215860611* output;
    output = calloc(1, sizeof(struct class_std_ClassInfo215860611));
    struct class_std_ClassInfo215860611_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_std_ClassInfo215860611_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1585224288;
    (*__vtable).equals1391760489 = std_ClassInfo_equals861649298;
    (*__vtable).drop115478603 = std_Object_drop1730782060;
    (*__vtable).toString1664535608 = std_Object_toString784171025;
    (*__vtable).equals1225076135 = std_ClassInfo_equals694964944;
    (*output).getClass2062954782 = std_Object_getClass216694119;
    (*output).getName37078109 = std_ClassInfo_getName493033082;
    (*output).is_object2127914142 = std_ClassInfo_is_object1597802951;
    (*output).is_class1664696309 = std_ClassInfo_is_class2100159796;
    (*output).info = (struct class_std_ClassInfo215860611*) __get_class(1);
    (*output).references = (long int) {0};
    (*output).name = (struct class_std_String708766606*) {0};
    (*output).parent = (struct class_std_ClassInfo215860611*) {0};
    (*output).classHash = (int) {0};
    return output;
}





static // bool super_std_Object_equals454500174 (std::Object*)
bool super_std_Object_equals4545001742135757981(void* __this, struct class_std_Object577384124* other) {
    struct class_std_ClassInfo215860611* this = __this;
    struct class_std_Object577384124* super = __this;
    bool (*old) (void*, struct class_std_Object577384124*);
    old = (*(*this).__vtable).equals1391760489;
    (*(*this).__vtable).equals1391760489 = std_Object_equals454500174;
    unsigned char output;
    output = (*(*this).__vtable).equals1391760489(__this, other);
    (*(*this).__vtable).equals1391760489 = old;
    return output;
}

struct class_std_String708766606;

struct class_std_String708766606_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals1391760489) (void*, struct class_std_Object577384124*);
	void (*drop115478603) (void*);
	struct class_std_String708766606* (*toString1664535608) (void*);
};
struct class_std_String708766606 {
	struct class_std_String708766606_vtable* __vtable;
	struct class_std_ClassInfo215860611* info;
	long int references;
	struct class_std_ClassInfo215860611* (*getClass2062954782) (void*);
	char* backingPtr;
	int length;
	struct class_std_String708766606* (*concat939353723) (void*, struct class_std_String708766606*);
	struct class_std_String708766606* (*concat1663899886) (void*, char*);
	struct class_std_String708766606* (*concat_integer631888426) (void*, long int);
	const char* (*getCStr36737184) (void*);
};

struct class_std_String708766606* std_String_concat1418625038(void*, struct class_std_String708766606*);
struct class_std_String708766606* std_String_concat694078875(void*, char*);
struct class_std_String708766606* std_String_concat_integer1305100109(void*, long int);
const char* std_String_getCStr1900251351(void*);
int std_Object_hashcode1585224288(void*);
bool std_Object_equals454500174(void*, struct class_std_Object577384124*);
void std_String_drop1821509932(void*);
struct class_std_String708766606* std_Object_toString784171025(void*);

struct class_std_String708766606* construct_std_String342477975_253337238(void*, const char*);
struct class_std_String708766606* construct_std_String0_1518088663(void*);

static struct class_std_String708766606* class_std_String708766606_init707239644() {
    struct class_std_String708766606* output;
    output = calloc(1, sizeof(struct class_std_String708766606));
    struct class_std_String708766606_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_std_String708766606_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1585224288;
    (*__vtable).equals1391760489 = std_Object_equals454500174;
    (*__vtable).drop115478603 = std_String_drop1821509932;
    (*__vtable).toString1664535608 = std_Object_toString784171025;
    (*output).getClass2062954782 = std_Object_getClass216694119;
    (*output).concat939353723 = std_String_concat1418625038;
    (*output).concat1663899886 = std_String_concat694078875;
    (*output).concat_integer631888426 = std_String_concat_integer1305100109;
    (*output).getCStr36737184 = std_String_getCStr1900251351;
    (*output).info = (struct class_std_ClassInfo215860611*) __get_class(2);
    (*output).references = (long int) {0};
    (*output).backingPtr = (char*) {0};
    (*output).length = (int) {0};
    return output;
}






static // void super_std_Object_drop1730782060 ()
void super_std_Object_drop17307820602030624382(void* __this) {
    struct class_std_String708766606* this = __this;
    struct class_std_Object577384124* super = __this;
    void (*old) (void*);
    old = (*(*this).__vtable).drop115478603;
    (*(*this).__vtable).drop115478603 = std_Object_drop1730782060;
    (*(*this).__vtable).drop115478603(__this);
    (*(*this).__vtable).drop115478603 = old;
}

typedef unsigned long int size_t;
void* memcpy(void*, const void*, size_t);
void* memmove(void*, const void*, size_t);
char* strcpy(char*, const char*);
char* strncpy(char*, const char*, size_t);
char* strcat(char*, const char*);
char* strncat(char*, const char*, size_t);
int memcmp(const void*, const void*, size_t);
int strcmp(const char*, const char*);
int strcoll(const char*, const char*);
int strncmp(const char*, const char*, size_t);
size_t strxfrm(char*, const char*, size_t);
char* strdup(const char*);
size_t strlen(const char*);
void print(const char* c) {
}

void println(const char* c) {
}

void print_s(struct class_std_String708766606* o) {
}

void println_s(struct class_std_String708766606* o) {
}


struct class_std_Object577384124* construct_std_Object0_317807437(void* __this) {
    struct class_std_Object577384124* this = (struct class_std_Object577384124*) __this;
    return this;
}

// int std::Object::hashcode ()
int std_Object_hashcode1585224288(void* __this) {
    struct class_std_Object577384124* this = __this;
    return (int) (void*) this;
}

// bool std::Object::equals (std::Object*)
bool std_Object_equals454500174(void* __this, struct class_std_Object577384124* other) {
    struct class_std_Object577384124* this = __this;
    return (this==other);
}

// std::String* std::Object::toString ()
struct class_std_String708766606* std_Object_toString784171025(void* __this) {
    struct class_std_Object577384124* this = __this;
    return ({
    	struct class_std_String708766606* __temp = ({
    	struct class_std_String708766606* __temp = ({
    	struct class_std_ClassInfo215860611* __temp = (*this).getClass2062954782(this);
    	(*__temp).getName37078109(__temp);
    });
    	(*__temp).concat1663899886(__temp, "@");
    });
    	(*__temp).concat939353723(__temp, (*this).__vtable->hashcode261036375(this));
    });
}

// void std::Object::drop ()
void std_Object_drop1730782060(void* __this) {
    struct class_std_Object577384124* this = __this;
    free(this);
}

// std::ClassInfo* std::Object::getClass ()
struct class_std_ClassInfo215860611* std_Object_getClass216694119(void* __this) {
    struct class_std_Object577384124* this = __this;
    return (*this).info;
}


struct class_std_ClassInfo215860611* construct_std_ClassInfo0_219382265(void* __this) {
    struct class_std_ClassInfo215860611* this = (struct class_std_ClassInfo215860611*) __this;
    return this;
}

// std::String* std::ClassInfo::getName ()
struct class_std_String708766606* std_ClassInfo_getName493033082(void* __this) {
    struct class_std_ClassInfo215860611* this = __this;
    struct class_std_Object577384124* super = __this;
    return (*this).name;
}

// bool std::ClassInfo::is_object (std::Object*)
bool std_ClassInfo_is_object1597802951(void* __this, struct class_std_Object577384124* o) {
    struct class_std_ClassInfo215860611* this = __this;
    struct class_std_Object577384124* super = __this;
    if ((o==nullptr)) return ((bool) 0);
    return (*this).is_class1664696309(this, (*o).getClass2062954782(o));
}

// bool std::ClassInfo::is_class (std::ClassInfo*)
bool std_ClassInfo_is_class2100159796(void* __this, struct class_std_ClassInfo215860611* o) {
    struct class_std_ClassInfo215860611* this = __this;
    struct class_std_Object577384124* super = __this;
    if ((*o).__vtable->equals1225076135(o, this)) return (!(bool) 0);
    if (((*o).parent!=nullptr)) {
        return (*this).is_class1664696309(this, (*o).parent);
    }
    return ((bool) 0);
}

// bool std::ClassInfo::equals (std::Object*)
bool std_ClassInfo_equals861649298(void* __this, struct class_std_Object577384124* other) {
    struct class_std_ClassInfo215860611* this = __this;
    struct class_std_Object577384124* super = __this;
    if (!other) return ((bool) 0);
    if (!({
    	struct class_std_ClassInfo215860611* __temp = (*this).getClass2062954782(this);
    	(*__temp).is_object2127914142(__temp, other);
    })) return ((bool) 0);
    return (*this).__vtable->equals1225076135(this, (struct class_std_ClassInfo215860611*) other);
}

// bool std::ClassInfo::equals (std::ClassInfo*)
bool std_ClassInfo_equals694964944(void* __this, struct class_std_ClassInfo215860611* other) {
    struct class_std_ClassInfo215860611* this = __this;
    struct class_std_Object577384124* super = __this;
    return ((*this).classHash==(*other).classHash);
}


struct class_std_String708766606* construct_std_String0_1518088663(void* __this) {
    construct_std_String342477975_253337238(__this, "");
    struct class_std_String708766606* this = (struct class_std_String708766606*) __this;
    return this;
}


struct class_std_String708766606* construct_std_String342477975_253337238(void* __this, const char* bp) {
    struct class_std_String708766606* this = (struct class_std_String708766606*) __this;
    (*this).backingPtr = strdup(bp);
    (*this).length = strlen(bp);
    return this;
}

// void std::String::drop ()
void std_String_drop1821509932(void* __this) {
    struct class_std_String708766606* this = __this;
    struct class_std_Object577384124* super = __this;
    super_std_Object_drop17307820602030624382(super);
}

// std::String* std::String::concat (std::String*)
struct class_std_String708766606* std_String_concat1418625038(void* __this, struct class_std_String708766606* other) {
    struct class_std_String708766606* this = __this;
    struct class_std_Object577384124* super = __this;
    char next[(((*this).length+(*other).length)+1)];
    strcpy(next, (*this).backingPtr);
    strcat(next, (*other).backingPtr);
    return construct_std_String342477975_253337238(class_std_String708766606_init707239644(), next);
}

// std::String* std::String::concat (char*)
struct class_std_String708766606* std_String_concat694078875(void* __this, char* other) {
    struct class_std_String708766606* this = __this;
    struct class_std_Object577384124* super = __this;
    return this;
}

// const char* std::String::getCStr ()
const char* std_String_getCStr1900251351(void* __this) {
    struct class_std_String708766606* this = __this;
    struct class_std_Object577384124* super = __this;
    return (*this).backingPtr;
}

// std::String* std::String::concat_integer (long int)
struct class_std_String708766606* std_String_concat_integer1305100109(void* __this, long int other) {
    struct class_std_String708766606* this = __this;
    struct class_std_Object577384124* super = __this;
    return this;
}

