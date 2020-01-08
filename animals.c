#include <stdlib.h>
void print(char* name);
void println(char* name);


void print(char*);
void println(char*);
struct class_animals_animal2086640357_vtable {
	int offset;
	void (*animals_animal_says1831075868) (void*);
};
struct class_animals_animal2086640357 {
	struct class_animals_animal2086640357_vtable* vtable;
	char* species;
	int numberOfLegs;
	int (*animals_animal_getNumberOfLegs718838711) (void*);
};

int animals_animal_getNumberOfLegs718838711(void*);
void animals_animal_says1831075868(void*);

struct class_animals_animal2086640357* construct_animals_animal1483334636_1702811642(void*, char*, int);

static struct class_animals_animal2086640357* class_animals_animal2086640357_init830776319() {
    struct class_animals_animal2086640357* output;
    output = malloc(sizeof(struct class_animals_animal2086640357));
    struct class_animals_animal2086640357_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_animal2086640357_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).animals_animal_says1831075868 = animals_animal_says1831075868;
    (*output).animals_animal_getNumberOfLegs718838711 = animals_animal_getNumberOfLegs718838711;
    return output;
}

int animals_animal_getNumberOfLegs718838711(void* __this) {
    struct class_animals_animal2086640357* this;
    this = (struct class_animals_animal2086640357*) __this;
    return (*this).numberOfLegs;
}




struct class_animals_animal2086640357* construct_animals_animal1483334636_1702811642(void* __this, char* species, int numberOfLegs) {
    struct class_animals_animal2086640357* this = (struct class_animals_animal2086640357*) __this;
    (*this).species = species;
    (*this).numberOfLegs = numberOfLegs;
    return this;
}

void animals_animal_says1831075868(void* __this) {
    struct class_animals_animal2086640357* this;
    this = (struct class_animals_animal2086640357*) __this;
    print((*this).species);
    print(" says ");
}


struct class_animals_quadAnimal474523892_vtable {
	int offset;
	void (*animals_quadAnimal_says2111014084) (void*);
};
struct class_animals_quadAnimal474523892 {
	struct class_animals_quadAnimal474523892_vtable* vtable;
	char* species;
	int numberOfLegs;
	int (*animals_animal_getNumberOfLegs718838711) (void*);
};

void animals_quadAnimal_says2111014084(void*);

struct class_animals_quadAnimal474523892* construct_animals_quadAnimal540585600_778842855(void*, char*);

static struct class_animals_quadAnimal474523892* class_animals_quadAnimal474523892_init1344579889() {
    struct class_animals_quadAnimal474523892* output;
    output = malloc(sizeof(struct class_animals_quadAnimal474523892));
    struct class_animals_quadAnimal474523892_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_quadAnimal474523892_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).animals_quadAnimal_says2111014084 = animals_quadAnimal_says2111014084;
    (*output).animals_animal_getNumberOfLegs718838711 = animals_animal_getNumberOfLegs718838711;
    return output;
}



struct class_animals_quadAnimal474523892* construct_animals_quadAnimal540585600_778842855(void* __this, char* name) {
    construct_animals_animal1483334636_1702811642(__this, name, 4);
    struct class_animals_quadAnimal474523892* this = (struct class_animals_quadAnimal474523892*) __this;
    return this;
}

static void super_animals_animal_says18310758681694836456(void* __this) {
    struct class_animals_quadAnimal474523892* this;
    this = (struct class_animals_quadAnimal474523892*) __this;
    struct class_animals_quadAnimal474523892* super;
    super = this;
    void (*old) (void*);
    old = (*(*this).vtable).animals_quadAnimal_says2111014084;
    (*(*this).vtable).animals_quadAnimal_says2111014084 = animals_animal_says1831075868;
    (*(*this).vtable).animals_quadAnimal_says2111014084(__this);
    (*(*this).vtable).animals_quadAnimal_says2111014084 = old;
}

void animals_quadAnimal_says2111014084(void* __this) {
    struct class_animals_quadAnimal474523892* this;
    this = (struct class_animals_quadAnimal474523892*) __this;
    struct class_animals_quadAnimal474523892* super;
    super = this;
    super_animals_animal_says18310758681694836456(super);
    print("I have 4 legs!");
}


struct class_animals_domesticated1052043033_vtable {
	int offset;
	void (*animals_quadAnimal_says2111014084) (void*);
};
struct class_animals_domesticated1052043033 {
	struct class_animals_domesticated1052043033_vtable* vtable;
	char* species;
	int numberOfLegs;
	int (*animals_animal_getNumberOfLegs718838711) (void*);
	char* name;
	char* (*animals_domesticated_getName657283203) (void*);
};

char* animals_domesticated_getName657283203(void*);
void animals_quadAnimal_says2111014084(void*);

struct class_animals_domesticated1052043033* construct_animals_domesticated2009014667_1821464887(void*, char*, char*);

static struct class_animals_domesticated1052043033* class_animals_domesticated1052043033_init1187487185() {
    struct class_animals_domesticated1052043033* output;
    output = malloc(sizeof(struct class_animals_domesticated1052043033));
    struct class_animals_domesticated1052043033_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_domesticated1052043033_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).animals_quadAnimal_says2111014084 = animals_quadAnimal_says2111014084;
    (*output).animals_animal_getNumberOfLegs718838711 = animals_animal_getNumberOfLegs718838711;
    (*output).animals_domesticated_getName657283203 = animals_domesticated_getName657283203;
    return output;
}

char* animals_domesticated_getName657283203(void* __this) {
    struct class_animals_domesticated1052043033* this;
    this = (struct class_animals_domesticated1052043033*) __this;
    struct class_animals_domesticated1052043033* super;
    super = this;
    return (*this).name;
}




struct class_animals_domesticated1052043033* construct_animals_domesticated2009014667_1821464887(void* __this, char* name, char* species) {
    construct_animals_quadAnimal540585600_778842855(__this, species);
    struct class_animals_domesticated1052043033* this = (struct class_animals_domesticated1052043033*) __this;
    (*this).name = name;
    return this;
}


struct class_animals_dog795110875_vtable {
	int offset;
	void (*animals_dog_says442165220) (void*);
};
struct class_animals_dog795110875 {
	struct class_animals_dog795110875_vtable* vtable;
	char* species;
	int numberOfLegs;
	int (*animals_animal_getNumberOfLegs718838711) (void*);
	char* name;
	char* (*animals_domesticated_getName657283203) (void*);
};

void animals_dog_says442165220(void*);

struct class_animals_dog795110875* construct_animals_dog1414521963_1883030382(void*, char*);
struct class_animals_dog795110875* construct_animals_dog1_552742565(void*);

static struct class_animals_dog795110875* class_animals_dog795110875_init927922561() {
    struct class_animals_dog795110875* output;
    output = malloc(sizeof(struct class_animals_dog795110875));
    struct class_animals_dog795110875_vtable* vtable;
    vtable = malloc(sizeof(struct class_animals_dog795110875_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).animals_dog_says442165220 = animals_dog_says442165220;
    (*output).animals_animal_getNumberOfLegs718838711 = animals_animal_getNumberOfLegs718838711;
    (*output).animals_domesticated_getName657283203 = animals_domesticated_getName657283203;
    return output;
}



struct class_animals_dog795110875* construct_animals_dog1414521963_1883030382(void* __this, char* name) {
    construct_animals_domesticated2009014667_1821464887(__this, name, "dog");
    struct class_animals_dog795110875* this = (struct class_animals_dog795110875*) __this;
    return this;
}


struct class_animals_dog795110875* construct_animals_dog1_552742565(void* __this) {
    construct_animals_dog1414521963_1883030382(__this, "unknown");
    struct class_animals_dog795110875* this = (struct class_animals_dog795110875*) __this;
    return this;
}

static void super_animals_quadAnimal_says2111014084557283921(void* __this) {
    struct class_animals_dog795110875* this;
    this = (struct class_animals_dog795110875*) __this;
    struct class_animals_dog795110875* super;
    super = this;
    void (*old) (void*);
    old = (*(*this).vtable).animals_dog_says442165220;
    (*(*this).vtable).animals_dog_says442165220 = animals_quadAnimal_says2111014084;
    (*(*this).vtable).animals_dog_says442165220(__this);
    (*(*this).vtable).animals_dog_says442165220 = old;
}

void animals_dog_says442165220(void* __this) {
    struct class_animals_dog795110875* this;
    this = (struct class_animals_dog795110875*) __this;
    struct class_animals_dog795110875* super;
    super = this;
    print((*this).animals_domesticated_getName657283203(this));
    print(" the ");
    super_animals_quadAnimal_says2111014084557283921(super);
    print(", also ARF");
}


int main() {
    struct class_animals_animal2086640357* griff = construct_animals_dog1414521963_1883030382(class_animals_dog795110875_init927922561(), "The Griff");
    (*griff).vtable->animals_animal_says1831075868(griff);
    return 0;
}

