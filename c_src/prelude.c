typedef unsigned long int class_id;
struct class_std_ClassInfo612126077;
struct class_std_ClassInfo612126077* __get_class(class_id);
struct class_std_String119220082;
typedef unsigned char bool;
void* malloc(unsigned int);
void* calloc(unsigned int, unsigned int);
void free(void*);
void exit(int);
void panic(struct class_std_String119220082*);
void print(const char*);
void println(const char*);
void print_s(struct class_std_String119220082*);
void println_s(struct class_std_String119220082*);
static const void* nullptr = 0;
struct class_std_Object250602564;

struct class_std_Object250602564_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals123258682) (void*, struct class_std_Object250602564*);
	void (*drop115478603) (void*);
	struct class_std_String119220082* (*toString1664535608) (void*);
};
struct class_std_Object250602564 {
	struct class_std_Object250602564_vtable* __vtable;
	struct class_std_ClassInfo612126077* info;
	long int references;
	struct class_std_ClassInfo612126077* (*getClass2062954782) (void*);
};

struct class_std_ClassInfo612126077* std_Object_getClass937603889(void*);
int std_Object_hashcode1555445000(void*);
bool std_Object_equals1417667307(void*, struct class_std_Object250602564*);
void std_Object_drop1409887228(void*);
struct class_std_String119220082* std_Object_toString370126983(void*);

struct class_std_Object250602564* construct_std_Object0_1574321893(void*);

static struct class_std_Object250602564* class_std_Object250602564_init1401757846() {
    struct class_std_Object250602564* output;
    output = calloc(1, sizeof(struct class_std_Object250602564));
    struct class_std_Object250602564_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_std_Object250602564_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1555445000;
    (*__vtable).equals123258682 = std_Object_equals1417667307;
    (*__vtable).drop115478603 = std_Object_drop1409887228;
    (*__vtable).toString1664535608 = std_Object_toString370126983;
    (*output).getClass2062954782 = std_Object_getClass937603889;
    (*output).info = (struct class_std_ClassInfo612126077*) __get_class(0);
    (*output).references = (long int) {0};
    return output;
}



struct class_std_ClassInfo612126077;

struct class_std_ClassInfo612126077_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals123258682) (void*, struct class_std_Object250602564*);
	void (*drop115478603) (void*);
	struct class_std_String119220082* (*toString1664535608) (void*);
	bool (*equals1773818959) (void*, struct class_std_ClassInfo612126077*);
};
struct class_std_ClassInfo612126077 {
	struct class_std_ClassInfo612126077_vtable* __vtable;
	struct class_std_ClassInfo612126077* info;
	long int references;
	struct class_std_ClassInfo612126077* (*getClass2062954782) (void*);
	struct class_std_String119220082* name;
	struct class_std_ClassInfo612126077* parent;
	int classHash;
	struct class_std_String119220082* (*getName37078109) (void*);
	bool (*is_object859412335) (void*, struct class_std_Object250602564*);
	bool (*is_class1115953485) (void*, struct class_std_ClassInfo612126077*);
};

struct class_std_String119220082* std_ClassInfo_getName1647331090(void*);
bool std_ClassInfo_is_object824996864(void*, struct class_std_Object250602564*);
bool std_ClassInfo_is_class1494604612(void*, struct class_std_ClassInfo612126077*);
int std_Object_hashcode1555445000(void*);
bool std_ClassInfo_equals1561150517(void*, struct class_std_Object250602564*);
void std_Object_drop1409887228(void*);
struct class_std_String119220082* std_Object_toString370126983(void*);
bool std_ClassInfo_equals89409760(void*, struct class_std_ClassInfo612126077*);

struct class_std_ClassInfo612126077* construct_std_ClassInfo0_1037132191(void*);

static struct class_std_ClassInfo612126077* class_std_ClassInfo612126077_init1481452115() {
    struct class_std_ClassInfo612126077* output;
    output = calloc(1, sizeof(struct class_std_ClassInfo612126077));
    struct class_std_ClassInfo612126077_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_std_ClassInfo612126077_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1555445000;
    (*__vtable).equals123258682 = std_ClassInfo_equals1561150517;
    (*__vtable).drop115478603 = std_Object_drop1409887228;
    (*__vtable).toString1664535608 = std_Object_toString370126983;
    (*__vtable).equals1773818959 = std_ClassInfo_equals89409760;
    (*output).getClass2062954782 = std_Object_getClass937603889;
    (*output).getName37078109 = std_ClassInfo_getName1647331090;
    (*output).is_object859412335 = std_ClassInfo_is_object824996864;
    (*output).is_class1115953485 = std_ClassInfo_is_class1494604612;
    (*output).info = (struct class_std_ClassInfo612126077*) __get_class(1);
    (*output).references = (long int) {0};
    (*output).name = (struct class_std_String119220082*) {0};
    (*output).parent = (struct class_std_ClassInfo612126077*) {0};
    (*output).classHash = (int) {0};
    return output;
}





static // bool super_std_Object_equals1417667307 (std::Object*)
bool super_std_Object_equals14176673071999440026(void* __this, struct class_std_Object250602564* other) {
    struct class_std_ClassInfo612126077* this = __this;
    struct class_std_Object250602564* super = __this;
    bool (*old) (void*, struct class_std_Object250602564*);
    old = (*(*this).__vtable).equals123258682;
    (*(*this).__vtable).equals123258682 = std_Object_equals1417667307;
    unsigned char output;
    output = (*(*this).__vtable).equals123258682(__this, other);
    (*(*this).__vtable).equals123258682 = old;
    return output;
}

struct class_std_String119220082;

struct class_std_String119220082_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals123258682) (void*, struct class_std_Object250602564*);
	void (*drop115478603) (void*);
	struct class_std_String119220082* (*toString1664535608) (void*);
};
struct class_std_String119220082 {
	struct class_std_String119220082_vtable* __vtable;
	struct class_std_ClassInfo612126077* info;
	long int references;
	struct class_std_ClassInfo612126077* (*getClass2062954782) (void*);
	char* backingPtr;
	int length;
	struct class_std_String119220082* (*concat87696842) (void*, struct class_std_String119220082*);
	struct class_std_String119220082* (*concat1663899886) (void*, char*);
	struct class_std_String119220082* (*concat_integer631888426) (void*, long int);
	const char* (*getCStr36737184) (void*);
};

struct class_std_String119220082* std_String_concat1115983911(void*, struct class_std_String119220082*);
struct class_std_String119220082* std_String_concat460219133(void*, char*);
struct class_std_String119220082* std_String_concat_integer1835569179(void*, long int);
const char* std_String_getCStr1240417937(void*);
int std_Object_hashcode1555445000(void*);
bool std_Object_equals1417667307(void*, struct class_std_Object250602564*);
void std_String_drop1319159356(void*);
struct class_std_String119220082* std_Object_toString370126983(void*);

struct class_std_String119220082* construct_std_String342477975_1509851694(void*, const char*);
struct class_std_String119220082* construct_std_String0_261574207(void*);

static struct class_std_String119220082* class_std_String119220082_init1697583293() {
    struct class_std_String119220082* output;
    output = calloc(1, sizeof(struct class_std_String119220082));
    struct class_std_String119220082_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_std_String119220082_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1555445000;
    (*__vtable).equals123258682 = std_Object_equals1417667307;
    (*__vtable).drop115478603 = std_String_drop1319159356;
    (*__vtable).toString1664535608 = std_Object_toString370126983;
    (*output).getClass2062954782 = std_Object_getClass937603889;
    (*output).concat87696842 = std_String_concat1115983911;
    (*output).concat1663899886 = std_String_concat460219133;
    (*output).concat_integer631888426 = std_String_concat_integer1835569179;
    (*output).getCStr36737184 = std_String_getCStr1240417937;
    (*output).info = (struct class_std_ClassInfo612126077*) __get_class(2);
    (*output).references = (long int) {0};
    (*output).backingPtr = (char*) {0};
    (*output).length = (int) {0};
    return output;
}






static // void super_std_Object_drop1409887228 ()
void super_std_Object_drop1409887228394420759(void* __this) {
    struct class_std_String119220082* this = __this;
    struct class_std_Object250602564* super = __this;
    void (*old) (void*);
    old = (*(*this).__vtable).drop115478603;
    (*(*this).__vtable).drop115478603 = std_Object_drop1409887228;
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

void print_s(struct class_std_String119220082* o) {
}

void println_s(struct class_std_String119220082* o) {
}


struct class_std_Object250602564* construct_std_Object0_1574321893(void* __this) {
    struct class_std_Object250602564* this = (struct class_std_Object250602564*) __this;
    return this;
}

// int std::Object::hashcode ()
int std_Object_hashcode1555445000(void* __this) {
    struct class_std_Object250602564* this = __this;
    return (int) (void*) this;
}

// bool std::Object::equals (std::Object*)
bool std_Object_equals1417667307(void* __this, struct class_std_Object250602564* other) {
    struct class_std_Object250602564* this = __this;
    return (this==other);
}

// std::String* std::Object::toString ()
struct class_std_String119220082* std_Object_toString370126983(void* __this) {
    struct class_std_Object250602564* this = __this;
    return ({
    	struct class_std_String119220082* __temp = ({
    	struct class_std_String119220082* __temp = ({
    	struct class_std_ClassInfo612126077* __temp = (*this).getClass2062954782(this);
    	(*__temp).getName37078109(__temp);
    });
    	(*__temp).concat1663899886(__temp, "@");
    });
    	(*__temp).concat87696842(__temp, (*this).__vtable->hashcode261036375(this));
    });
}

// void std::Object::drop ()
void std_Object_drop1409887228(void* __this) {
    struct class_std_Object250602564* this = __this;
    free(this);
}

// std::ClassInfo* std::Object::getClass ()
struct class_std_ClassInfo612126077* std_Object_getClass937603889(void* __this) {
    struct class_std_Object250602564* this = __this;
    return (*this).info;
}


struct class_std_ClassInfo612126077* construct_std_ClassInfo0_1037132191(void* __this) {
    struct class_std_ClassInfo612126077* this = (struct class_std_ClassInfo612126077*) __this;
    return this;
}

// std::String* std::ClassInfo::getName ()
struct class_std_String119220082* std_ClassInfo_getName1647331090(void* __this) {
    struct class_std_ClassInfo612126077* this = __this;
    struct class_std_Object250602564* super = __this;
    return (*this).name;
}

// bool std::ClassInfo::is_object (std::Object*)
bool std_ClassInfo_is_object824996864(void* __this, struct class_std_Object250602564* o) {
    struct class_std_ClassInfo612126077* this = __this;
    struct class_std_Object250602564* super = __this;
    if ((o==nullptr)) return ((bool) 0);
    return (*this).is_class1115953485(this, (*o).getClass2062954782(o));
}

// bool std::ClassInfo::is_class (std::ClassInfo*)
bool std_ClassInfo_is_class1494604612(void* __this, struct class_std_ClassInfo612126077* o) {
    struct class_std_ClassInfo612126077* this = __this;
    struct class_std_Object250602564* super = __this;
    if ((*o).__vtable->equals1773818959(o, this)) return (!(bool) 0);
    if (((*o).parent!=nullptr)) {
        return (*this).is_class1115953485(this, (*o).parent);
    }
    return ((bool) 0);
}

// bool std::ClassInfo::equals (std::Object*)
bool std_ClassInfo_equals1561150517(void* __this, struct class_std_Object250602564* other) {
    struct class_std_ClassInfo612126077* this = __this;
    struct class_std_Object250602564* super = __this;
    if (!other) return ((bool) 0);
    if (!({
    	struct class_std_ClassInfo612126077* __temp = (*this).getClass2062954782(this);
    	(*__temp).is_object859412335(__temp, other);
    })) return ((bool) 0);
    return (*this).__vtable->equals1773818959(this, (struct class_std_ClassInfo612126077*) other);
}

// bool std::ClassInfo::equals (std::ClassInfo*)
bool std_ClassInfo_equals89409760(void* __this, struct class_std_ClassInfo612126077* other) {
    struct class_std_ClassInfo612126077* this = __this;
    struct class_std_Object250602564* super = __this;
    return ((*this).classHash==(*other).classHash);
}


struct class_std_String119220082* construct_std_String0_261574207(void* __this) {
    construct_std_String342477975_1509851694(__this, "");
    struct class_std_String119220082* this = (struct class_std_String119220082*) __this;
    return this;
}


struct class_std_String119220082* construct_std_String342477975_1509851694(void* __this, const char* bp) {
    struct class_std_String119220082* this = (struct class_std_String119220082*) __this;
    (*this).backingPtr = strdup(bp);
    (*this).length = strlen(bp);
    return this;
}

// void std::String::drop ()
void std_String_drop1319159356(void* __this) {
    struct class_std_String119220082* this = __this;
    struct class_std_Object250602564* super = __this;
    super_std_Object_drop1409887228394420759(super);
}

// std::String* std::String::concat (std::String*)
struct class_std_String119220082* std_String_concat1115983911(void* __this, struct class_std_String119220082* other) {
    struct class_std_String119220082* this = __this;
    struct class_std_Object250602564* super = __this;
    char next[(((*this).length+(*other).length)+1)];
    strcpy(next, (*this).backingPtr);
    strcat(next, (*other).backingPtr);
    return construct_std_String342477975_1509851694(class_std_String119220082_init1697583293(), next);
}

// std::String* std::String::concat (char*)
struct class_std_String119220082* std_String_concat460219133(void* __this, char* other) {
    struct class_std_String119220082* this = __this;
    struct class_std_Object250602564* super = __this;
    return this;
}

// const char* std::String::getCStr ()
const char* std_String_getCStr1240417937(void* __this) {
    struct class_std_String119220082* this = __this;
    struct class_std_Object250602564* super = __this;
    return (*this).backingPtr;
}

// std::String* std::String::concat_integer (long int)
struct class_std_String119220082* std_String_concat_integer1835569179(void* __this, long int other) {
    struct class_std_String119220082* this = __this;
    struct class_std_Object250602564* super = __this;
    return this;
}

