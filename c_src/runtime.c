typedef unsigned long int class_id;
struct class_std_ClassInfo66005309;
struct class_std_ClassInfo66005309* __get_class(class_id);
struct class_std_String426900686;
typedef unsigned char bool;
void* malloc(unsigned int);
void* calloc(unsigned int, unsigned int);
void free(void*);
void exit(int);
void panic(struct class_std_String426900686*);
void print(const char*);
void println(const char*);
void print_s(struct class_std_String426900686*);
void println_s(struct class_std_String426900686*);
static const void* nullptr = 0;
struct class_std_Object295518204;

struct class_std_Object295518204_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals754063962) (void*, struct class_std_Object295518204*);
	void (*drop115478603) (void*);
	struct class_std_String426900686* (*toString1664535608) (void*);
};
struct class_std_Object295518204 {
	struct class_std_Object295518204_vtable* __vtable;
	struct class_std_ClassInfo66005309* info;
	long int references;
	struct class_std_ClassInfo66005309* (*getClass2062954782) (void*);
};

struct class_std_ClassInfo66005309* std_Object_getClass87671311(void*);
int std_Object_hashcode1889589718(void*);
bool std_Object_equals1396562131(void*, struct class_std_Object295518204*);
void std_Object_drop2035147490(void*);
struct class_std_String426900686* std_Object_toString479805595(void*);

struct class_std_Object295518204* construct_std_Object0_474263939(void*);

static struct class_std_Object295518204* class_std_Object295518204_init1190376232() {
    struct class_std_Object295518204* output;
    output = calloc(1, sizeof(struct class_std_Object295518204));
    struct class_std_Object295518204_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_std_Object295518204_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1889589718;
    (*__vtable).equals754063962 = std_Object_equals1396562131;
    (*__vtable).drop115478603 = std_Object_drop2035147490;
    (*__vtable).toString1664535608 = std_Object_toString479805595;
    (*output).getClass2062954782 = std_Object_getClass87671311;
    (*output).info = (struct class_std_ClassInfo66005309*) __get_class(0);
    (*output).references = (long int) {0};
    return output;
}



struct class_std_ClassInfo66005309;

struct class_std_ClassInfo66005309_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals754063962) (void*, struct class_std_Object295518204*);
	void (*drop115478603) (void*);
	struct class_std_String426900686* (*toString1664535608) (void*);
	bool (*equals2042027338) (void*, struct class_std_ClassInfo66005309*);
};
struct class_std_ClassInfo66005309 {
	struct class_std_ClassInfo66005309_vtable* __vtable;
	struct class_std_ClassInfo66005309* info;
	long int references;
	struct class_std_ClassInfo66005309* (*getClass2062954782) (void*);
	struct class_std_String426900686* name;
	struct class_std_ClassInfo66005309* parent;
	int classHash;
	struct class_std_String426900686* (*getName37078109) (void*);
	bool (*is_object1490217615) (void*, struct class_std_Object295518204*);
	bool (*is_class636832486) (void*, struct class_std_ClassInfo66005309*);
};

struct class_std_String426900686* std_ClassInfo_getName797398512(void*);
bool std_ClassInfo_is_object655740994(void*, struct class_std_Object295518204*);
bool std_ClassInfo_is_class1471309107(void*, struct class_std_ClassInfo66005309*);
int std_Object_hashcode1889589718(void*);
bool std_ClassInfo_equals80412659(void*, struct class_std_Object295518204*);
void std_Object_drop2035147490(void*);
struct class_std_String426900686* std_Object_toString479805595(void*);
bool std_ClassInfo_equals1418463337(void*, struct class_std_ClassInfo66005309*);

struct class_std_ClassInfo66005309* construct_std_ClassInfo0_62925763(void*);

static struct class_std_ClassInfo66005309* class_std_ClassInfo66005309_init925819020() {
    struct class_std_ClassInfo66005309* output;
    output = calloc(1, sizeof(struct class_std_ClassInfo66005309));
    struct class_std_ClassInfo66005309_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_std_ClassInfo66005309_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1889589718;
    (*__vtable).equals754063962 = std_ClassInfo_equals80412659;
    (*__vtable).drop115478603 = std_Object_drop2035147490;
    (*__vtable).toString1664535608 = std_Object_toString479805595;
    (*__vtable).equals2042027338 = std_ClassInfo_equals1418463337;
    (*output).getClass2062954782 = std_Object_getClass87671311;
    (*output).getName37078109 = std_ClassInfo_getName797398512;
    (*output).is_object1490217615 = std_ClassInfo_is_object655740994;
    (*output).is_class636832486 = std_ClassInfo_is_class1471309107;
    (*output).info = (struct class_std_ClassInfo66005309*) __get_class(1);
    (*output).references = (long int) {0};
    (*output).name = (struct class_std_String426900686*) {0};
    (*output).parent = (struct class_std_ClassInfo66005309*) {0};
    (*output).classHash = (int) {0};
    return output;
}





static // bool super_std_Object_equals1396562131 (std::Object*)
bool super_std_Object_equals13965621311384087597(void* __this, struct class_std_Object295518204* other) {
    struct class_std_ClassInfo66005309* this = __this;
    struct class_std_Object295518204* super = __this;
    bool (*old) (void*, struct class_std_Object295518204*);
    old = (*(*this).__vtable).equals754063962;
    (*(*this).__vtable).equals754063962 = std_Object_equals1396562131;
    unsigned char output;
    output = (*(*this).__vtable).equals754063962(__this, other);
    (*(*this).__vtable).equals754063962 = old;
    return output;
}

struct class_std_String426900686;

struct class_std_String426900686_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals754063962) (void*, struct class_std_Object295518204*);
	void (*drop115478603) (void*);
	struct class_std_String426900686* (*toString1664535608) (void*);
};
struct class_std_String426900686 {
	struct class_std_String426900686_vtable* __vtable;
	struct class_std_ClassInfo66005309* info;
	long int references;
	struct class_std_ClassInfo66005309* (*getClass2062954782) (void*);
	char* backingPtr;
	int length;
	struct class_std_String426900686* (*concat1557676490) (void*, struct class_std_String426900686*);
	struct class_std_String426900686* (*concat1663899886) (void*, char*);
	struct class_std_String426900686* (*concat_integer631888426) (void*, long int);
	const char* (*getCStr36737184) (void*);
};

struct class_std_String426900686* std_String_concat495936841(void*, struct class_std_String426900686*);
struct class_std_String426900686* std_String_concat389713445(void*, char*);
struct class_std_String426900686* std_String_concat_integer1609465539(void*, long int);
const char* std_String_getCStr2090350515(void*);
int std_Object_hashcode1889589718(void*);
bool std_Object_equals1396562131(void*, struct class_std_Object295518204*);
void std_String_drop2125875362(void*);
struct class_std_String426900686* std_Object_toString479805595(void*);

struct class_std_String426900686* construct_std_String342477975_409793740(void*, const char*);
struct class_std_String426900686* construct_std_String0_1361632161(void*);

static struct class_std_String426900686* class_std_String426900686_init1560060449() {
    struct class_std_String426900686* output;
    output = calloc(1, sizeof(struct class_std_String426900686));
    struct class_std_String426900686_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_std_String426900686_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1889589718;
    (*__vtable).equals754063962 = std_Object_equals1396562131;
    (*__vtable).drop115478603 = std_String_drop2125875362;
    (*__vtable).toString1664535608 = std_Object_toString479805595;
    (*output).getClass2062954782 = std_Object_getClass87671311;
    (*output).concat1557676490 = std_String_concat495936841;
    (*output).concat1663899886 = std_String_concat389713445;
    (*output).concat_integer631888426 = std_String_concat_integer1609465539;
    (*output).getCStr36737184 = std_String_getCStr2090350515;
    (*output).info = (struct class_std_ClassInfo66005309*) __get_class(2);
    (*output).references = (long int) {0};
    (*output).backingPtr = (char*) {0};
    (*output).length = (int) {0};
    return output;
}






static // void super_std_Object_drop2035147490 ()
void super_std_Object_drop20351474901412168739(void* __this) {
    struct class_std_String426900686* this = __this;
    struct class_std_Object295518204* super = __this;
    void (*old) (void*);
    old = (*(*this).__vtable).drop115478603;
    (*(*this).__vtable).drop115478603 = std_Object_drop2035147490;
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
typedef unsigned char byte;
struct j_heap {
	byte* jheap;
	size_t size;
	size_t bytes_free;
};
static struct j_heap {
	byte* jheap;
	size_t size;
	size_t bytes_free;
} jodin_heap;
void panic(struct class_std_String426900686* message) {
    println_s(message);
    exit(-1);
}

bool __init_heap() {
    jodin_heap.jheap = calloc(sizeof(byte), (1028*(1028*128)));
    if ((jodin_heap.jheap==nullptr)) {
        return ((bool) 0);
    }


    jodin_heap.size = (1028*(1028*128));
    jodin_heap.bytes_free = 0;
    return (!(bool) 0);
}

void __init_reflection();
int __main(int, struct class_std_String426900686*[]);
static struct class_std_ClassInfo66005309* class_std_ClassInfo66005309_info = 0;
static struct class_std_ClassInfo66005309* class_std_Object295518204_info = 0;
static struct class_std_ClassInfo66005309* class_std_String426900686_info = 0;
static struct class_std_ClassInfo66005309* class_ll_Node2060947710_info = 0;
static struct class_std_ClassInfo66005309* class_ll_IntLinkedList355477606_info = 0;
struct class_std_ClassInfo66005309* __get_class(class_id id) {
    if ((id==1)) return class_std_ClassInfo66005309_info;
    if ((id==0)) return class_std_Object295518204_info;
    if ((id==2)) return class_std_String426900686_info;
    if ((id==6)) return class_ll_Node2060947710_info;
    if ((id==7)) return class_ll_IntLinkedList355477606_info;
    return nullptr;
}

int main(int argc, char* argv[]) {
    __init_reflection();
    struct class_std_String426900686* args[argc];
    for (int i = 0;(i<argc); i++)args[i] = construct_std_String342477975_409793740(class_std_String426900686_init1560060449(), argv[i]);
    int output = __main(argc, args);
    for (int i = 0;(i<argc); i++)(*args[i]).__vtable->drop115478603(args[i]);
    return output;
}

void __init_reflection() {
    class_std_ClassInfo66005309_info = construct_std_ClassInfo0_62925763(class_std_ClassInfo66005309_init925819020());
    (*class_std_ClassInfo66005309_info).name = construct_std_String342477975_409793740(class_std_String426900686_init1560060449(), "std::ClassInfo");
    (*class_std_ClassInfo66005309_info).classHash = -2046164579;
    (*class_std_ClassInfo66005309_info).info = class_std_ClassInfo66005309_info;
    class_std_Object295518204_info = construct_std_ClassInfo0_62925763(class_std_ClassInfo66005309_init925819020());
    (*class_std_Object295518204_info).name = construct_std_String342477975_409793740(class_std_String426900686_init1560060449(), "std::Object");
    (*class_std_Object295518204_info).classHash = 571129732;
    class_std_String426900686_info = construct_std_ClassInfo0_62925763(class_std_ClassInfo66005309_init925819020());
    (*class_std_String426900686_info).name = construct_std_String342477975_409793740(class_std_String426900686_init1560060449(), "std::String");
    (*class_std_String426900686_info).classHash = 349019378;
    class_ll_Node2060947710_info = construct_std_ClassInfo0_62925763(class_std_ClassInfo66005309_init925819020());
    (*class_ll_Node2060947710_info).name = construct_std_String342477975_409793740(class_std_String426900686_init1560060449(), "ll::Node");
    (*class_ll_Node2060947710_info).classHash = 535130430;
    class_ll_IntLinkedList355477606_info = construct_std_ClassInfo0_62925763(class_std_ClassInfo66005309_init925819020());
    (*class_ll_IntLinkedList355477606_info).name = construct_std_String342477975_409793740(class_std_String426900686_init1560060449(), "ll::IntLinkedList");
    (*class_ll_IntLinkedList355477606_info).classHash = -1865096102;
    (*class_std_ClassInfo66005309_info).parent = __get_class(0);
    (*class_std_Object295518204_info).parent = nullptr;
    (*class_std_String426900686_info).parent = __get_class(0);
    (*class_ll_Node2060947710_info).parent = __get_class(0);
    (*class_ll_IntLinkedList355477606_info).parent = __get_class(0);
}

