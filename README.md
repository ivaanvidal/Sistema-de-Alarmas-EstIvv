# Sistema-de-Alarmas-EstIvv


Sistema de Alarmas para Proyecto  Titulo Universidad del Bio Bio

Esta Aplicación se basa en  una vinculación con un Dispositivo Arduino, el cual estará conectado a una Base de Datos Mysql,
este mismo cambiará un valor de un Usuario en la Base de Datos a "Desactivado" o "Activado", junto a este ultimo valor se enviará una
Notificacion mediante Firebase a la App del Usuario con una prioridad 'High'.

La App de dicho Usuario detectará la Notificación enviada por Firebase, incluso estando el telefono en modo Dormido (Mode Doze), para 
lograr que la app tenga acceso a Internet y cosulte a la BD Mysql el Valor cambiado por el arduino, de encontrar que el valor fue 
cambiado a "Activado", la app ejecutará una serie de Alarmas como Sonido+Vibracion, hasta que desde la App esto se desactive presionando el Boton "Desactivar" manualmente, lo que hará cambiar el valor de la BD y detener las Alarmas (Sonido + Vibracion), O la otra opcion es
que el Arduino vuelva a cambiar el valor en la BD a "Desactivado" y lo cual la app detectará y apagará las alarmas automáticamente...
Hasta un proximo cambio a "Activado" por el Arduino...

Cabe mencionar que la app hace uso de una 'Autenticación de Firebase' usando Emails; tambien contiene implementado un 'Servicio' ejecutado 
en primer plano con una 'Notificacion de Importancia High', esto para que la aplicación se mantenga siempre en ejecución hasta que el 
propio usuario cierre la sesión manualmente y esta será la unica forma de detener la app; Tambien tiene implementado las 'Notificaciones de 
Firebase' para lograr despertar el telefono y tenga acceso a internet incluso estando en Modo Dormido (Mode Doze) y hacer las consultas a 
la BD; y por ultimo una serie de consultas volleys hacia la Base de Datos Mysql.

By Ivan Vidal Sepulveda.
