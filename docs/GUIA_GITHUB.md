# Guía de GitHub para el proyecto

La guía del curso pide que el proyecto esté en un repositorio GitHub y que lo entreguen comprimido.

## 1. Crear el repositorio
1. Entra a https://github.com/new, ponle un nombre (por ejemplo `gestor-activos`) y elige **Private** o **Public** según indique el docente.
2. No marques "Add a README" (el proyecto ya trae uno).
3. Si trabajan en grupo: *Settings → Collaborators → Add people* para invitar a cada integrante, y comparte el enlace con el docente.

## 2. Subir el proyecto por primera vez
Abre una terminal en la carpeta del proyecto (la que contiene `pom.xml`) y ejecuta:

```bash
git init
git add .
git commit -m "Avance 2: aplicación web de gestión de activos"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/gestor-activos.git
git push -u origin main
```

La primera vez, Git abrirá una ventana para iniciar sesión en GitHub. El archivo `.gitignore` ya excluye `target/` y los archivos privados de NetBeans.

> Antes de subir, revisa `src/main/resources/db.properties`: si usas una contraseña real de MySQL, cámbiala por una de prueba (o por la genérica `root`).

## 3. Hacerlo desde NetBeans
1. Clic derecho en el proyecto → *Versioning → Initialize Git Repository*.
2. *Git → Commit…* escribe el mensaje y confirma.
3. *Git → Remote → Push…* pega la URL del repositorio y elige la rama `main`.

## 4. Trabajo coordinado en grupo
Cada integrante trabaja en su propia rama y la une a `main` mediante un Pull Request:

```bash
git clone https://github.com/TU_USUARIO/gestor-activos.git      # solo la primera vez
git pull                                                         # traer lo último antes de empezar
git checkout -b feature/modulo-empleados                         # rama propia
# ...programar...
git add .
git commit -m "Agrega validación de DNI en empleados"
git push -u origin feature/modulo-empleados
```
Luego, en GitHub, pulsa **Compare & pull request**, pide revisión a un compañero y usa **Merge**. Después, cada uno hace `git checkout main` y `git pull`.

Buenas prácticas:
- Commits pequeños, con mensajes que digan qué cambió ("Corrige filtro de estado", no "cambios").
- Hacer `git pull` antes de empezar a trabajar para evitar conflictos.
- No subir `target/`, contraseñas ni archivos personales.

## 5. Entrega
1. Copia la URL del repositorio en el documento técnico (sección 6) y en la última diapositiva.
2. Comprime el código fuente: clic derecho en la carpeta del proyecto → *Enviar a → Carpeta comprimida*. Antes, borra la carpeta `target/`.
