MASSBITROUTE_CORE_IP=$(cat MASSBITROUTE_CORE_IP)
MASSBITROUTE_PORTAL_IP=$(cat MASSBITROUTE_PORTAL_IP)
MASSBITROUTE_RUST_IP=$(cat MASSBITROUTE_RUST_IP)

cat hosts-template | \
    sed "s/\[\[MASSBITROUTE_CORE_IP\]\]/$MASSBITROUTE_CORE_IP/g" | \
    sed "s/\[\[MASSBITROUTE_PORTAL_IP\]\]/$MASSBITROUTE_PORTAL_IP/g" | \
    sed "s/\[\[MASSBITROUTE_RUST_IP\]\]/$MASSBITROUTE_RUST_IP/g" > test-hosts

