typedef unsigned long int class_id;
struct class_std_ClassInfo1868018659;
struct class_std_ClassInfo1868018659* __get_class(class_id);
struct class_std_String1934042642;
typedef unsigned char bool;
void* malloc(unsigned int);
void* calloc(unsigned int, unsigned int);
void free(void*);
void print(const char*);
void println(const char*);
void print_s(struct class_std_String1934042642*);
void println_s(struct class_std_String1934042642*);
const void* nullptr = 0;
struct class_std_Object2065425124;

struct class_std_Object2065425124_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals270661635) (void*, struct class_std_Object2065425124*);
	void (*drop115478603) (void*);
	struct class_std_String1934042642* (*toString1664535608) (void*);
};
struct class_std_Object2065425124 {
	struct class_std_Object2065425124_vtable* vtable;
	struct class_std_ClassInfo1868018659* info;
	long int references;
	struct class_std_ClassInfo1868018659* (*getClass2062954782) (void*);
};

struct class_std_ClassInfo1868018659* std_Object_getClass683927334(void*);
int std_Object_hashcode1809121555(void*);
bool std_Object_equals1818746815(void*, struct class_std_Object2065425124*);
void std_Object_drop1663563783(void*);
struct class_std_String1934042642* std_Object_toString116450428(void*);

struct class_std_Object2065425124* construct_std_Object1_895720795(void*);

static struct class_std_Object2065425124* class_std_Object2065425124_init1710329940() {
    struct class_std_Object2065425124* output;
    output = calloc(1, sizeof(struct class_std_Object2065425124));
    struct class_std_Object2065425124_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_Object2065425124_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode1809121555;
    (*vtable).equals270661635 = std_Object_equals1818746815;
    (*vtable).drop115478603 = std_Object_drop1663563783;
    (*vtable).toString1664535608 = std_Object_toString116450428;
    (*output).getClass2062954782 = std_Object_getClass683927334;
    (*output).info = (struct class_std_ClassInfo1868018659*) __get_class(0);
    (*output).references = (long int) {0};
    return output;
}



struct class_std_ClassInfo1868018659;

struct class_std_ClassInfo1868018659_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals270661635) (void*, struct class_std_Object2065425124*);
	void (*drop115478603) (void*);
	struct class_std_String1934042642* (*toString1664535608) (void*);
	bool (*equals1441879473) (void*, struct class_std_ClassInfo1868018659*);
};
struct class_std_ClassInfo1868018659 {
	struct class_std_ClassInfo1868018659_vtable* vtable;
	struct class_std_ClassInfo1868018659* info;
	long int references;
	struct class_std_ClassInfo1868018659* (*getClass2062954782) (void*);
	struct class_std_String1934042642* name;
	struct class_std_ClassInfo1868018659* parent;
	int classHash;
	struct class_std_String1934042642* (*getName37078109) (void*);
	int (*is1566147950) (void*, struct class_std_Object2065425124*);
};

struct class_std_String1934042642* std_ClassInfo_getName1393654535(void*);
int std_ClassInfo_is135415306(void*, struct class_std_Object2065425124*);
int std_Object_hashcode1809121555(void*);
bool std_ClassInfo_equals1160071009(void*, struct class_std_Object2065425124*);
void std_Object_drop1663563783(void*);
struct class_std_String1934042642* std_Object_toString116450428(void*);
bool std_ClassInfo_equals11146829(void*, struct class_std_ClassInfo1868018659*);

struct class_std_ClassInfo1868018659* construct_std_ClassInfo206518852_1649433708(void*, struct class_std_String1934042642*, struct class_std_ClassInfo1868018659*, int);

static struct class_std_ClassInfo1868018659* class_std_ClassInfo1868018659_init128441368() {
    struct class_std_ClassInfo1868018659* output;
    output = calloc(1, sizeof(struct class_std_ClassInfo1868018659));
    struct class_std_ClassInfo1868018659_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_ClassInfo1868018659_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode1809121555;
    (*vtable).equals270661635 = std_ClassInfo_equals1160071009;
    (*vtable).drop115478603 = std_Object_drop1663563783;
    (*vtable).toString1664535608 = std_Object_toString116450428;
    (*vtable).equals1441879473 = std_ClassInfo_equals11146829;
    (*output).getClass2062954782 = std_Object_getClass683927334;
    (*output).getName37078109 = std_ClassInfo_getName1393654535;
    (*output).is1566147950 = std_ClassInfo_is135415306;
    (*output).info = (struct class_std_ClassInfo1868018659*) __get_class(1);
    (*output).references = (long int) {0};
    (*output).name = (struct class_std_String1934042642*) {0};
    (*output).parent = (struct class_std_ClassInfo1868018659*) {0};
    (*output).classHash = (int) {0};
    return output;
}




static // bool super_std_Object_equals1818746815 (CXClass std::Object*)
bool super_std_Object_equals1818746815804854323(void* __this, struct class_std_Object2065425124* other) {
    struct class_std_ClassInfo1868018659* this = __this;
    struct class_std_Object2065425124* super = __this;
    bool (*old) (void*, struct class_std_Object2065425124*);
    old = (*(*this).vtable).equals270661635;
    (*(*this).vtable).equals270661635 = std_Object_equals1818746815;
    unsigned char output;
    output = (*(*this).vtable).equals270661635(__this, other);
    (*(*this).vtable).equals270661635 = old;
    return output;
}

struct class_std_String1934042642;

struct class_std_String1934042642_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals270661635) (void*, struct class_std_Object2065425124*);
	void (*drop115478603) (void*);
	struct class_std_String1934042642* (*toString1664535608) (void*);
};
struct class_std_String1934042642 {
	struct class_std_String1934042642_vtable* vtable;
	struct class_std_ClassInfo1868018659* info;
	long int references;
	struct class_std_ClassInfo1868018659* (*getClass2062954782) (void*);
	char* backingPtr;
	int length;
	struct class_std_String1934042642* (*concat1780151184) (void*, struct class_std_String1934042642*);
	struct class_std_String1934042642* (*concat1663899886) (void*, char*);
	struct class_std_String1934042642* (*concat_integer631888426) (void*, long int);
	const char* (*getCStr36737184) (void*);
};

struct class_std_String1934042642* std_String_concat1057458804(void*, struct class_std_String1934042642*);
struct class_std_String1934042642* std_String_concat206542578(void*, char*);
struct class_std_String1934042642* std_String_concat_integer2089245734(void*, long int);
const char* std_String_getCStr1494094492(void*);
int std_Object_hashcode1809121555(void*);
bool std_Object_equals1818746815(void*, struct class_std_Object2065425124*);
void std_String_drop1572835911(void*);
struct class_std_String1934042642* std_Object_toString116450428(void*);

struct class_std_String1934042642* construct_std_String627150512_408124148(void*, const char*);
struct class_std_String1934042642* construct_std_String1_940175305(void*);

static struct class_std_String1934042642* class_std_String1934042642_init1576822812() {
    struct class_std_String1934042642* output;
    output = calloc(1, sizeof(struct class_std_String1934042642));
    struct class_std_String1934042642_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_String1934042642_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode1809121555;
    (*vtable).equals270661635 = std_Object_equals1818746815;
    (*vtable).drop115478603 = std_String_drop1572835911;
    (*vtable).toString1664535608 = std_Object_toString116450428;
    (*output).getClass2062954782 = std_Object_getClass683927334;
    (*output).concat1780151184 = std_String_concat1057458804;
    (*output).concat1663899886 = std_String_concat206542578;
    (*output).concat_integer631888426 = std_String_concat_integer2089245734;
    (*output).getCStr36737184 = std_String_getCStr1494094492;
    (*output).info = (struct class_std_ClassInfo1868018659*) __get_class(2);
    (*output).references = (long int) {0};
    (*output).backingPtr = (char*) {0};
    (*output).length = (int) {0};
    return output;
}






static // void super_std_Object_drop1663563783 ()
void super_std_Object_drop16635637831792863145(void* __this) {
    struct class_std_String1934042642* this = __this;
    struct class_std_Object2065425124* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).drop115478603;
    (*(*this).vtable).drop115478603 = std_Object_drop1663563783;
    (*(*this).vtable).drop115478603(__this);
    (*(*this).vtable).drop115478603 = old;
}

struct class_Blob1075714055;

struct class_Blob1075714055_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals270661635) (void*, struct class_std_Object2065425124*);
	void (*drop115478603) (void*);
	struct class_std_String1934042642* (*toString1664535608) (void*);
};
struct class_Blob1075714055 {
	struct class_Blob1075714055_vtable* vtable;
	struct class_std_ClassInfo1868018659* info;
	long int references;
	struct class_std_ClassInfo1868018659* (*getClass2062954782) (void*);
	int x;
};

int std_Object_hashcode1809121555(void*);
bool std_Object_equals1818746815(void*, struct class_std_Object2065425124*);
void std_Object_drop1663563783(void*);
struct class_std_String1934042642* std_Object_toString116450428(void*);


static struct class_Blob1075714055* class_Blob1075714055_init1296121520() {
    struct class_Blob1075714055* output;
    output = calloc(1, sizeof(struct class_Blob1075714055));
    struct class_Blob1075714055_vtable* vtable;
    vtable = malloc(sizeof(struct class_Blob1075714055_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode1809121555;
    (*vtable).equals270661635 = std_Object_equals1818746815;
    (*vtable).drop115478603 = std_Object_drop1663563783;
    (*vtable).toString1664535608 = std_Object_toString116450428;
    (*output).getClass2062954782 = std_Object_getClass683927334;
    (*output).info = (struct class_std_ClassInfo1868018659*) __get_class(8);
    (*output).references = (long int) {0};
    (*output).x = (int) {0};
    return output;
}


