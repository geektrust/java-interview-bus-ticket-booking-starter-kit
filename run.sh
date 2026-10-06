#! /bin/bash
ant clean && ant dist && java -jar dist/lib/dist.jar "$@"