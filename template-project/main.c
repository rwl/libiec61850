/*
 *  main.c
 *
 *  Main file of the template project. Use this as a starting point to create your own projects.
 *
 */

#include "iec61850_simple_server_api.h"
#include "thread.h"
#include <signal.h>
#include <stdlib.h>
#include <stdio.h>

/* import IEC 61850 device model created from SCL-File */
extern IedModel staticIedModel;

static int running = 0;

void sigint_handler(int signalId)
{
	running = 0;
}

int main(int argc, char** argv) {

	IedServer iedServer = IedServer_create(&staticIedModel);

	/* This will create default values for the complete IEC model and installs them in the
	 * MMS server's value cache.
	 */
	IedServer_setAllModelDefaultValues(iedServer);


	/* MMS server will be instructed to start listening to client connections. */
	IedServer_start(iedServer);


	running = 1;

	signal(SIGINT, sigint_handler);

	/* Main loop - here we do nothing. IedServer handles all requests by itself */
	while (running) {
		Thread_sleep(1);
	}

	/* stop MMS server - close TCP server socket and all client sockets */
	IedServer_stop(iedServer);

	/* Cleanup - free all resources */
	IedServer_destroy(iedServer);
} /* main() */
