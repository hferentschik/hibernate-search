# Mac static route installation script

## Why this script

The scripts in this directory define a new Mac startup script which will on each restart install the required
multicast route entry for the loop-back device. This is required for some JGroups based tests and per default
Mac OS X does not define the required root per default. See also [HSEARCH-1558](https://hibernate.atlassian.net/browse/HSEARCH-1558).

## How to run

In order to install the script run:

    > sudo ./install-multicast-routes.sh


