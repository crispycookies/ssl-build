DESCRIPTION = "userspace daemon"
SECTION = "base"
LICENSE = "GPLv3"
FILESEXTRAPATHS_prepend := "${THISDIR}/files/:"

SRC_URI = "git://github.com/crispycookies/SSLYocto.git;protocol=https;branch=M3_userapplication"

SRCREV = "${AUTOREV}"

SYSTEMD_SERVICE_${PN} = "userspace.service"

DEPENDS = "paho-mqtt3a paho-mqtt3c paho-mqttpp3 fpgaregion pthread"
RDEPENDS_${PN} = "paho-mqtt-cpp"

TARGET_CC_ARCH += "${LDFLAGS}"

inherit systemd

GIT = "${WORKDIR}/git"
S = "${GIT}"
inherit pkgconfig cmake
#MAKEFILECONFIG = "YOCTO"
#EXTRA_OEMAKE += "'CONFIG=${MAKEFILECONFIG}'"

do_compile () {
    oe_runmake
}

do_install() {
    # install service file
    install -d ${D}${systemd_unitdir}/system
    install -c -m 0644 ${WORKDIR}/git/userspace.service ${D}${systemd_unitdir}/system

    # install binary
    install -d ${D}${bindir}
    install -c -m 0755 ${WORKDIR}/build/UserspaceApp ${D}${bindir}/UserspaceApp

    # install empty firmware dir if not existing to store fpga bitfile
    install -d -m 0755 ${D}${base_libdir}/firmware

    # install config
    install -d ${D}${sysconfdir}/UserspaceApp
}

FILES_${PN} = "${base_libdir}/systemd/system/userspace.service"
FILES_${PN} += "${bindir}/UserspaceApp"
FILES_${PN} += "${sysconfdir}/UserspaceApp"
FILES_${PN} += "${base_libdir}/firmware"

# As this package is tied to systemd, only build it when we're also building systemd.
python () {
    if not bb.utils.contains ('DISTRO_FEATURES', 'systemd', True, False, d):
        raise bb.parse.SkipPackage("'systemd' not in DISTRO_FEATURES")
}
