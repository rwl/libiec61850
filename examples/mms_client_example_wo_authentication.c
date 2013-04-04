
#include <stdlib.h>
#include <stdio.h>
#include "mms_client_connection.h"

int main(int argc, char** argv) {

	char* hostname;
	int tcpPort = 102;

	if (argc > 1)
		hostname = argv[1];
	else
		hostname = "localhost";

	if (argc > 2)
		tcpPort = atoi(argv[2]);

	MmsConnection con = MmsConnection_create();

	MmsIndication indication;

	indication = MmsConnection_connect(con, hostname, tcpPort);

	if (indication != MMS_OK) {
		printf("MMS connect failed!\n");
		goto exit;
	}
	else
		printf("MMS connected.\n\n");

	printf("Domains present on server:\n--------------------------\n");
	LinkedList nameList = MmsConnection_getDomainNames(con);
	LinkedList_printStringList(nameList);
	LinkedList_destroy(nameList);
	printf("\n");


exit:
	MmsConnection_destroy(con);
}

