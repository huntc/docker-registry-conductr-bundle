EXPERIMENTAL - DO NOT USE

# Example Docker Registry as a ConductR Bundle

Example of running a Docker v2 registry container on ConductR

## Use

Run `sbt bundle:dist` generate a bundle that can be loaded and started in [ConductR](https://conductr.typesafe.com).

Right now just one instance can be scaled as the registry will require further work to be backed by Redis (for example).

The bundle will make the docker registry available on `$SERVICE_PROXY:5000`. $SERVICE_PROXY indicates where ConductR's proxy is running. It is important to use the proxy's IP address as bundle execution i.e. other bundles that require the registry, may not be running on the same machine; particularly in a Mesos situation.

Our goal is for another bundle to be able to address the ConductR hosted repository like so `service-proxy:5000/ubuntu` (supposing it is the Ubuntu image to be addressed). In order to achieve this the Docker daemon on each node where a bundle may execute must be configured to accept `service-proxy:5000` without TLS and without authentication i.e. the service-proxy based registry is trusted within the ConductR cluster. From [the Docker documentation](https://github.com/docker/distribution/blob/master/docs/insecure.md) then:

1. edit the file `/etc/default/docker` so that there is a line that reads: `DOCKER\_OPTS="--insecure-registry service-proxy:5000"` (or add that to existing `DOCKER_OPTS`)
2. restart your Docker daemon: on ubuntu, this is usually service docker restart

Add an entry to your /etc/hosts (just for this experiment) so that `service-proxy` resolves to `10.0.2.15`.

As an exercise, once you load and run the registry on ConductR, do the following in order to copy a cached image into the new registry (we'll copy the registry image):

```
> docker pull registry
> docker images
REPOSITORY                                                                                    TAG                           IMAGE ID            CREATED             VIRTUAL SIZE
registry                                                                                      2                             2f1ef7702586        13 days ago         220.6 MB
```

`2f1ef7702586` must now be tagged with the private registry:

```
docker tag 2f1ef7702586 service-proxy:5000/registry
```

You are now in a position to push it to the ConductR hosted registry:

```
docker push service-proxy:5000/registry
```

For fun, let's pull that image back down:

```
docker pull service-proxy:5000/registry
```

### Bundles

For bundles to be able to resolve addresses of the form `service-proxy:5000/xxx` where `xxx` is the image, you need to add the `service-proxy` resolution to the address of a proxy. For example, each machine that runs a bundle should have a `service-proxy` entry for a proxy in its `/etc/hosts` file, or establish a DNS server that holds entries for all known proxies.
