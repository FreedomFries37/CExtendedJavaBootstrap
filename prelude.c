typedef unsigned long int class_id;
struct class_std_ClassInfo1569038691;
struct class_std_ClassInfo1569038691* __get_class(class_id);
struct class_std_String2061944686;
typedef unsigned char bool;
void* malloc(unsigned int);
void* calloc(unsigned int, unsigned int);
void free(void*);
void print(const char*);
void println(const char*);
void print_s(struct class_std_String2061944686*);
void println_s(struct class_std_String2061944686*);
static const void* nullptr = 0;
struct class_std_Object1930562204;

struct class_std_Object1930562204_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals1993419246) (void*, struct class_std_Object1930562204*);
	void (*drop115478603) (void*);
	struct class_std_String2061944686* (*toString1664535608) (void*);
};
struct class_std_Object1930562204 {
	struct class_std_Object1930562204_vtable* vtable;
	struct class_std_ClassInfo1569038691* info;
	long int references;
	struct class_std_ClassInfo1569038691* (*getClass2062954782) (void*);
};

struct class_std_ClassInfo1569038691* std_Object_getClass1804912570(void*);
int std_Object_hashcode688136319(void*);
bool std_Object_equals1566319302(void*, struct class_std_Object1930562204*);
void std_Object_drop542578547(void*);
struct class_std_String2061944686* std_Object_toString1237435664(void*);

struct class_std_Object1930562204* construct_std_Object0_1338261646(void*);

static struct class_std_Object1930562204* class_std_Object1930562204_init574546521() {
    struct class_std_Object1930562204* output;
    output = calloc(1, sizeof(struct class_std_Object1930562204));
    struct class_std_Object1930562204_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_Object1930562204_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode688136319;
    (*vtable).equals1993419246 = std_Object_equals1566319302;
    (*vtable).drop115478603 = std_Object_drop542578547;
    (*vtable).toString1664535608 = std_Object_toString1237435664;
    (*output).getClass2062954782 = std_Object_getClass1804912570;
    (*output).info = (struct class_std_ClassInfo1569038691*) __get_class(0);
    (*output).references = (long int) {0};
    return output;
}



struct class_std_ClassInfo1569038691;

struct class_std_ClassInfo1569038691_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals1993419246) (void*, struct class_std_Object1930562204*);
	void (*drop115478603) (void*);
	struct class_std_String2061944686* (*toString1664535608) (void*);
	bool (*equals463475075) (void*, struct class_std_ClassInfo1569038691*);
};
struct class_std_ClassInfo1569038691 {
	struct class_std_ClassInfo1569038691_vtable* vtable;
	struct class_std_ClassInfo1569038691* info;
	long int references;
	struct class_std_ClassInfo1569038691* (*getClass2062954782) (void*);
	struct class_std_String2061944686* name;
	struct class_std_ClassInfo1569038691* parent;
	int classHash;
	struct class_std_String2061944686* (*getName37078109) (void*);
	bool (*is697932931) (void*, struct class_std_Object1930562204*);
	bool (*is_class941719777) (void*, struct class_std_ClassInfo1569038691*);
};

struct class_std_String2061944686* std_ClassInfo_getName1780327525(void*);
bool std_ClassInfo_is1045316485(void*, struct class_std_Object1930562204*);
bool std_ClassInfo_is_class1609998103(void*, struct class_std_ClassInfo1569038691*);
int std_Object_hashcode688136319(void*);
bool std_ClassInfo_equals250169830(void*, struct class_std_Object1930562204*);
void std_Object_drop542578547(void*);
struct class_std_String2061944686* std_Object_toString1237435664(void*);
bool std_ClassInfo_equals1279774341(void*, struct class_std_ClassInfo1569038691*);

struct class_std_ClassInfo1569038691* construct_std_ClassInfo0_801071944(void*);

static struct class_std_ClassInfo1569038691* class_std_ClassInfo1569038691_init263626320() {
    struct class_std_ClassInfo1569038691* output;
    output = calloc(1, sizeof(struct class_std_ClassInfo1569038691));
    struct class_std_ClassInfo1569038691_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_ClassInfo1569038691_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode688136319;
    (*vtable).equals1993419246 = std_ClassInfo_equals250169830;
    (*vtable).drop115478603 = std_Object_drop542578547;
    (*vtable).toString1664535608 = std_Object_toString1237435664;
    (*vtable).equals463475075 = std_ClassInfo_equals1279774341;
    (*output).getClass2062954782 = std_Object_getClass1804912570;
    (*output).getName37078109 = std_ClassInfo_getName1780327525;
    (*output).is697932931 = std_ClassInfo_is1045316485;
    (*output).is_class941719777 = std_ClassInfo_is_class1609998103;
    (*output).info = (struct class_std_ClassInfo1569038691*) __get_class(1);
    (*output).references = (long int) {0};
    (*output).name = (struct class_std_String2061944686*) {0};
    (*output).parent = (struct class_std_ClassInfo1569038691*) {0};
    (*output).classHash = (int) {0};
    return output;
}





static // bool super_std_Object_equals1566319302 (CXClass std::Object*)
bool super_std_Object_equals15663193021752503099(void* __this, struct class_std_Object1930562204* other) {
    struct class_std_ClassInfo1569038691* this = __this;
    struct class_std_Object1930562204* super = __this;
    bool (*old) (void*, struct class_std_Object1930562204*);
    old = (*(*this).vtable).equals1993419246;
    (*(*this).vtable).equals1993419246 = std_Object_equals1566319302;
    unsigned char output;
    output = (*(*this).vtable).equals1993419246(__this, other);
    (*(*this).vtable).equals1993419246 = old;
    return output;
}

struct class_std_String2061944686;

struct class_std_String2061944686_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals1993419246) (void*, struct class_std_Object1930562204*);
	void (*drop115478603) (void*);
	struct class_std_String2061944686* (*toString1664535608) (void*);
};
struct class_std_String2061944686 {
	struct class_std_String2061944686_vtable* vtable;
	struct class_std_ClassInfo1569038691* info;
	long int references;
	struct class_std_ClassInfo1569038691* (*getClass2062954782) (void*);
	char* backingPtr;
	int length;
	struct class_std_String2061944686* (*concat909588885) (void*, struct class_std_String2061944686*);
	struct class_std_String2061944686* (*concat1663899886) (void*, char*);
	struct class_std_String2061944686* (*concat_integer631888426) (void*, long int);
	const char* (*getCStr36737184) (void*);
};

struct class_std_String2061944686* std_String_concat573216813(void*, struct class_std_String2061944686*);
struct class_std_String2061944686* std_String_concat1327527814(void*, char*);
struct class_std_String2061944686* std_String_concat_integer968260498(void*, long int);
const char* std_String_getCStr373109256(void*);
int std_Object_hashcode688136319(void*);
bool std_Object_equals1566319302(void*, struct class_std_Object1930562204*);
void std_String_drop451850675(void*);
struct class_std_String2061944686* std_Object_toString1237435664(void*);

struct class_std_String2061944686* construct_std_String342477975_1273791447(void*, const char*);
struct class_std_String2061944686* construct_std_String0_497634454(void*);

static struct class_std_String2061944686* class_std_String2061944686_init168299349() {
    struct class_std_String2061944686* output;
    output = calloc(1, sizeof(struct class_std_String2061944686));
    struct class_std_String2061944686_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_String2061944686_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode688136319;
    (*vtable).equals1993419246 = std_Object_equals1566319302;
    (*vtable).drop115478603 = std_String_drop451850675;
    (*vtable).toString1664535608 = std_Object_toString1237435664;
    (*output).getClass2062954782 = std_Object_getClass1804912570;
    (*output).concat909588885 = std_String_concat573216813;
    (*output).concat1663899886 = std_String_concat1327527814;
    (*output).concat_integer631888426 = std_String_concat_integer968260498;
    (*output).getCStr36737184 = std_String_getCStr373109256;
    (*output).info = (struct class_std_ClassInfo1569038691*) __get_class(2);
    (*output).references = (long int) {0};
    (*output).backingPtr = (char*) {0};
    (*output).length = (int) {0};
    return output;
}






static // void super_std_Object_drop542578547 ()
void super_std_Object_drop54257854782936184(void* __this) {
    struct class_std_String2061944686* this = __this;
    struct class_std_Object1930562204* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).drop115478603;
    (*(*this).vtable).drop115478603 = std_Object_drop542578547;
    (*(*this).vtable).drop115478603(__this);
    (*(*this).vtable).drop115478603 = old;
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

void print_s(struct class_std_String2061944686* o) {
}

void println_s(struct class_std_String2061944686* o) {
}


struct class_std_Object1930562204* construct_std_Object0_1338261646(void* __this) {
    struct class_std_Object1930562204* this = (struct class_std_Object1930562204*) __this;
    return this;
}

// int std::Object::hashcode ()
int std_Object_hashcode688136319(void* __this) {
    struct class_std_Object1930562204* this = __this;
    return (int) (void*) this;
}

// bool std::Object::equals (CXClass std::Object*)
bool std_Object_equals1566319302(void* __this, struct class_std_Object1930562204* other) {
    struct class_std_Object1930562204* this = __this;
    return (this==other);
}

// CXClass std::String* std::Object::toString ()
struct class_std_String2061944686* std_Object_toString1237435664(void* __this) {
    struct class_std_Object1930562204* this = __this;
    return ({
    	struct class_std_String2061944686* __temp = ({
    	struct class_std_String2061944686* __temp = ({
    	struct class_std_ClassInfo1569038691* __temp = (*this).getClass2062954782(this);
    	(*__temp).getName37078109(__temp);
    });
    	(*__temp).concat1663899886(__temp, "@");
    });
    	(*__temp).concat909588885(__temp, (*this).vtable->hashcode261036375(this));
    });
}

// void std::Object::drop ()
void std_Object_drop542578547(void* __this) {
    struct class_std_Object1930562204* this = __this;
    free(this);
}

// CXClass std::ClassInfo* std::Object::getClass ()
struct class_std_ClassInfo1569038691* std_Object_getClass1804912570(void* __this) {
    struct class_std_Object1930562204* this = __this;
    return (*this).info;
}


struct class_std_ClassInfo1569038691* construct_std_ClassInfo0_801071944(void* __this) {
    struct class_std_ClassInfo1569038691* this = (struct class_std_ClassInfo1569038691*) __this;
    return this;
}

// CXClass std::String* std::ClassInfo::getName ()
struct class_std_String2061944686* std_ClassInfo_getName1780327525(void* __this) {
    struct class_std_ClassInfo1569038691* this = __this;
    struct class_std_Object1930562204* super = __this;
    return (*this).name;
}

// bool std::ClassInfo::is (CXClass std::Object*)
bool std_ClassInfo_is1045316485(void* __this, struct class_std_Object1930562204* o) {
    struct class_std_ClassInfo1569038691* this = __this;
    struct class_std_Object1930562204* super = __this;
    if ((o==nullptr)) return ((bool) 0);
    return (*this).is_class941719777(this, (*o).getClass2062954782(o));
}

// bool std::ClassInfo::is_class (CXClass std::ClassInfo*)
bool std_ClassInfo_is_class1609998103(void* __this, struct class_std_ClassInfo1569038691* o) {
    struct class_std_ClassInfo1569038691* this = __this;
    struct class_std_Object1930562204* super = __this;
    if ((*o).vtable->equals463475075(o, this)) return (!(bool) 0);
    if (((*o).parent!=nullptr)) {
        return (*this).is_class941719777(this, (*o).parent);
    }
    return ((bool) 0);
}

// bool std::ClassInfo::equals (CXClass std::Object*)
bool std_ClassInfo_equals250169830(void* __this, struct class_std_Object1930562204* other) {
    struct class_std_ClassInfo1569038691* this = __this;
    struct class_std_Object1930562204* super = __this;
    if (!other) return ((bool) 0);
    if (!({
    	struct class_std_ClassInfo1569038691* __temp = (*this).getClass2062954782(this);
    	(*__temp).is697932931(__temp, other);
    })) return ((bool) 0);
    return (*this).vtable->equals463475075(this, (struct class_std_ClassInfo1569038691*) other);
}

// bool std::ClassInfo::equals (CXClass std::ClassInfo*)
bool std_ClassInfo_equals1279774341(void* __this, struct class_std_ClassInfo1569038691* other) {
    struct class_std_ClassInfo1569038691* this = __this;
    struct class_std_Object1930562204* super = __this;
    return ((*this).classHash==(*other).classHash);
}


struct class_std_String2061944686* construct_std_String0_497634454(void* __this) {
    construct_std_String342477975_1273791447(__this, "");
    struct class_std_String2061944686* this = (struct class_std_String2061944686*) __this;
    return this;
}


struct class_std_String2061944686* construct_std_String342477975_1273791447(void* __this, const char* bp) {
    struct class_std_String2061944686* this = (struct class_std_String2061944686*) __this;
    (*this).backingPtr = strdup(bp);
    (*this).length = strlen(bp);
    return this;
}

// void std::String::drop ()
void std_String_drop451850675(void* __this) {
    struct class_std_String2061944686* this = __this;
    struct class_std_Object1930562204* super = __this;
    super_std_Object_drop54257854782936184(super);
}

// CXClass std::String* std::String::concat (CXClass std::String*)
struct class_std_String2061944686* std_String_concat573216813(void* __this, struct class_std_String2061944686* other) {
    struct class_std_String2061944686* this = __this;
    struct class_std_Object1930562204* super = __this;
    char next[(((*this).length+(*other).length)+1)];
    strcpy(next, (*this).backingPtr);
    strcat(next, (*other).backingPtr);
    return construct_std_String342477975_1273791447(class_std_String2061944686_init168299349(), next);
}

// CXClass std::String* std::String::concat (char*)
struct class_std_String2061944686* std_String_concat1327527814(void* __this, char* other) {
    struct class_std_String2061944686* this = __this;
    struct class_std_Object1930562204* super = __this;
    return this;
}

// const char* std::String::getCStr ()
const char* std_String_getCStr373109256(void* __this) {
    struct class_std_String2061944686* this = __this;
    struct class_std_Object1930562204* super = __this;
    return (*this).backingPtr;
}

// CXClass std::String* std::String::concat_integer (long int)
struct class_std_String2061944686* std_String_concat_integer968260498(void* __this, long int other) {
    struct class_std_String2061944686* this = __this;
    struct class_std_Object1930562204* super = __this;
    return this;
}

