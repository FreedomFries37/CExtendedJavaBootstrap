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

void __init_reflection();
int __main(int, struct class_std_String2061944686*[]);
static struct class_std_ClassInfo1569038691* class_std_ClassInfo1569038691_info = 0;
static struct class_std_ClassInfo1569038691* class_std_Object1930562204_info = 0;
static struct class_std_ClassInfo1569038691* class_std_String2061944686_info = 0;
static struct class_std_ClassInfo1569038691* class_std_Inner354100877_info = 0;
struct class_std_ClassInfo1569038691* __get_class(class_id id) {
    if ((id==1)) return class_std_ClassInfo1569038691_info;
    if ((id==0)) return class_std_Object1930562204_info;
    if ((id==2)) return class_std_String2061944686_info;
    if ((id==6)) return class_std_Inner354100877_info;
    return nullptr;
}

int main(int argc, char* argv[]) {
    __init_reflection();
    struct class_std_String2061944686* args[argc];
    for (int i = 0;(i<argc); i++)args[i] = construct_std_String342477975_1273791447(class_std_String2061944686_init168299349(), argv[i]);
    int output = __main(argc, args);
    for (int i = 0;(i<argc); i++)(*args[i]).vtable->drop115478603(args[i]);
    return output;
}

void __init_reflection() {
    class_std_ClassInfo1569038691_info = construct_std_ClassInfo0_801071944(class_std_ClassInfo1569038691_init263626320());
    (*class_std_ClassInfo1569038691_info).name = construct_std_String342477975_1273791447(class_std_String2061944686_init168299349(), "std::ClassInfo");
    (*class_std_ClassInfo1569038691_info).classHash = 1395559165;
    (*class_std_ClassInfo1569038691_info).info = class_std_ClassInfo1569038691_info;
    class_std_Object1930562204_info = construct_std_ClassInfo0_801071944(class_std_ClassInfo1569038691_init263626320());
    (*class_std_Object1930562204_info).name = construct_std_String342477975_1273791447(class_std_String2061944686_init168299349(), "std::Object");
    (*class_std_Object1930562204_info).classHash = -282113820;
    class_std_String2061944686_info = construct_std_ClassInfo0_801071944(class_std_ClassInfo1569038691_init263626320());
    (*class_std_String2061944686_info).name = construct_std_String342477975_1273791447(class_std_String2061944686_init168299349(), "std::String");
    (*class_std_String2061944686_info).classHash = -504224174;
    class_std_Inner354100877_info = construct_std_ClassInfo0_801071944(class_std_ClassInfo1569038691_init263626320());
    (*class_std_Inner354100877_info).name = construct_std_String342477975_1273791447(class_std_String2061944686_init168299349(), "std::Inner");
    (*class_std_Inner354100877_info).classHash = 1907774701;
    (*class_std_ClassInfo1569038691_info).parent = __get_class(0);
    (*class_std_Object1930562204_info).parent = nullptr;
    (*class_std_String2061944686_info).parent = __get_class(0);
    (*class_std_Inner354100877_info).parent = __get_class(0);
}

