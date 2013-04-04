/*
 *  simple_server_example2.c
 *
 *  Example server application using the simple API.
 *  This examples shows how to set multiple vales at once by using the IedServer locking functions.
 *  This mechanism can be used to implement transactions on the server side. E.g. a measurement
 *  value and its corresponding time stamp and quality values should be updated simultaneously.
 *
 *  Copyright 2013 Michael Zillgith
 *
 *	This file is part of libIEC61850.
 *
 *	libIEC61850 is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	libIEC61850 is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with libIEC61850.  If not, see <http://www.gnu.org/licenses/>.
 *
 *	See COPYING file for the complete license text.
 */

#include "iec61850_simple_server_api.h"
#include "thread.h"
#include <signal.h>

/* import IEC 61850 device model created from SCL-File */
extern IedModel staticIedModel;

static int running = 0;

void sigint_handler(int sign)
{
	running = 0;
}

int main(int argc, char** argv) {

	IedServer iedServer = IedServer_create(&staticIedModel);

	/* This will create default values for the complete IEC model and installs them in the
	 * MMS server's value cache.
	 */
	IedServer_setAllModelDefaultValues(iedServer);

	/* Get the MMS domain for an IEC 61850 Logical Device */
	MmsDomain* sampleDevice = IedServer_getDomain(iedServer, "SampleIEDDevice1");

	/* Get the reference to the structured value object */
	MmsValue* totW = IedServer_getValue(iedServer, sampleDevice, "MMXU2$MX$TotW");

	/* get references to the relevant elements of the copy */
	MmsValue* totW_mag_f = MmsValue_getStructElementByIndex(MmsValue_getStructElementByIndex(totW, 0), 0);
	MmsValue* totW_t = MmsValue_getStructElementByIndex(totW, 2);

	/* MMS server will be instructed to start listening to client connections. */
	IedServer_start(iedServer);
	running = 1;

	signal(SIGINT, sigint_handler);

	float totW_value = 0.f;

	while (running) {

		/* Lock the data model - access from MMS Clients will be blocked */
		IedServer_lockDataModel(iedServer);

		/* Update measurement values */
		MmsValue_setFloat(totW_mag_f, totW_value);
		MmsValue_setUtcTime(totW_t, time(NULL));

		/* Unlock the data model - access from MMS clients will be processed */
		IedServer_unlockDataModel(iedServer);

		totW_value += 0.1f;
		Thread_sleep(1000);
	}

	/* stop MMS server - close TCP server socket and all client sockets */
	IedServer_stop(iedServer);

	/* Cleanup - free all resources */
	IedServer_destroy(iedServer);
} /* main() */
