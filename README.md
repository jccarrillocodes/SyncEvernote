# SyncEvernote
Caso de prueba donde se va a utilizar la API de Evernote para obtener y crear notas

## Requisitos
- Android Studio 1.5+
- SDK Android 23+

## Casos
- Inicio de sesión con login
- Listado de notas creadas
- Ordenar notas por fecha
- Mostrar contenido de la nota
- Añadir nota
- TODO: reconocimiento OCR

## Pasos

### Investigación evernote
https://github.com/evernote/evernote-sdk-android/blob/master/README.md

### Creación de repositorio
Aplicación genérica de Android Studio con un Activity vacío, se le han añadido las librerías necesarias al Gradle

### Login
El requestCode no es el especificado en su API, así que se ha utilizado el devuelto en sandbox

### Fragment con el listado de notas
Se listan todas las notas de todos los Notebook, no se especifica a que Notebook pertenecen

### Fragment para añadir nota
Debido a tiempos, se descarta realizar integración con motor OCR, sin embargo, se ha añadido el motor de reconocimiento de voz de Google

#Developed By

* Juan Carlos Carrillo - http://jccarrillo.es - <cto@jccarrillo.es>


#License

    Copyright 2015 Juan Carlos Carrillo

    Licensed under the GNU General Public License;
