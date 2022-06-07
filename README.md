# NotaPlus - Julio José Meijueiro Dacosta
### Proyecto de Desarrollo de Aplicaciones Multiplataforma

<br>

## Versión 1.0
### Cambios generales

- `MainActivity` : interfaz, panel lateral y funcionalidad inicial.
- `CrearNotaActivity` : interfaz y funcionalidad inicial.
- Creada base de datos y tabla para las notas.
- Creados temas claro y oscuro.
- Creado logotipo. <img src="https://github.com/JJMD1999/NotaPlus/blob/master/app/src/main/ic_icono_app-playstore.png" width="25" height="25">

### Funciones añadidas

- Añadir nota a base de datos.
- Mostrar notas en `MainActivity` (RecyclerView, Adapter y ViewHolder).
- Añadidos diferentes colores para las notas en `CrearNotaActivity` (Layout desplegable).
- Cambiar entre temas mediante _switch_ en `MainActivity`.

### Cambios menores

- Agregadas fuentes.
- Personalización de la interfaz.
- Botones de acceso rápido (sin funcionalidad todavía).
- Menú con opciones en el panel lateral (sin funcionalidad todavía).

<br><hr><br>

## Versión 2.0
### Funciones añadidas

- Añadir imagen desde galería del dispositivo.
- Visualizar la imagen desde la vista previa en la interfaz principal.
- Añadir hipervínculo mediante `AlertDialog` a la nota que redirige a la web.
- Ambas opciones se realizan desde el desplegable en `CrearNotaActivity`.

### Cambios menores

- Pequeño cambio de diseño en interfaz principal.

<br><hr><br>

## Versión 3.0
### Funciones añadidas

- Añadida la función de seleccionar una nota desde la vista de previa y mostrarla.
- Añadida la función de editar una nota y guardar los cambios.
- Ya se pueden eliminar las imágenes y los enlaces de las notas.
- Creada `SettingsActivity` desde dónde configurar preferencias de la aplicación.

### Cambios menores

- Añadido el botón `Opciones` al menú lateral.
- Eliminado el icono de cambiar de tema (Sustituído por `SettingsActivity`).
- Personalización de la interfaz.
- Corregido un problema con la interfaz al añadir una imagen a una nota.
- Corregido un problema que, al asignar un color a una nota, este se asignaba a todas las notas anteriores si estas no tenían ningún color seleccionado.

<br><hr><br>

## Versión 4.0
### Funciones añadidas

- Añadida la función de eliminar una nota.
- Añadida la opción de buscar y filtrar notas mediante un `SearchView`.
- Añadida funcionalidad al Menú lateral. Ahora permite visuaizar notas archivadas y en la papelera.
- Etiquetas agregadas a las notas. Ya se pueden añadir etiquetas a las notas y filtrar por ellas.

### Cambios menores

- Cambiados disposición de elementos visuales.
- Agregados elementos visuales intuitivos para la edición de las notas.

<br><hr><br>

## Versión 5.0
### Funciones añadidas

- Añadida la función de voz a texto
- Añadidas las funciones de los accesos rápidos.
- Añadidos los recordatorios con notificaciones.
- Traducción al `inglés` añadida.

### Cambios menores

- Agregadas consultas adicionales al ***DAO***.
- Cambiado el funcionamiento de las opciones del menú.
- Cambiado el icono de los recordatorios y *Eliminar definitivamente*.
- Uso de más cadenas desde *values/strings.xml* en vez de cadenas de texto explícitas.
- Corregido un problema que impedía actualizar la vista de las notas al enviar una nota a archivo o a la papelera y viceversa.

<br><hr><br>

## Versión 6.0
### Funciones añadidas

- Añadida adaptabilidad de tamaño para distintos dispositivos.
- Añadidos tipos de fuentes.

### Cambios menores

- Mayor legibilidad en el código.
- Corregido un problema al cambiar el tema de la aplicación que no se actualizaba el color de las notas.
