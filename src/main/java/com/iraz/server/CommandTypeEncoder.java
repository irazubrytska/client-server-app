package com.iraz.server;

import lombok.Getter;

public class CommandTypeEncoder {
    static public final int
            PRODUCT = 1,
            GROUP = 2;

    static public final int
            CREATE           = 4,
            READ             = 8,
            UPDATE           = 16,
            DELETE           = 32,
            LIST_BY_CRITERIA = 64;

    static public final int
            PRODUCT_CREATE           = PRODUCT ^ CREATE,
            PRODUCT_READ             = PRODUCT ^ READ,
            PRODUCT_UPDATE           = PRODUCT ^ UPDATE,
            PRODUCT_DELETE           = PRODUCT ^ DELETE,
            PRODUCT_LIST_BY_CRITERIA = PRODUCT ^ LIST_BY_CRITERIA;

    static public final int
            GROUP_CREATE           = GROUP ^ CREATE,
            GROUP_READ             = GROUP ^ READ,
            GROUP_UPDATE           = GROUP ^ UPDATE,
            GROUP_DELETE           = GROUP ^ DELETE,
            GROUP_LIST_BY_CRITERIA = GROUP ^ LIST_BY_CRITERIA;

    static boolean isProduct(int INCOMING_COMMAND_TYPE) {
        return (INCOMING_COMMAND_TYPE & PRODUCT) == 1;
    }

    static int getTypeCommandCode(int INCOMING_COMMAND_TYPE) {
        boolean IS_PRODUCT = isProduct(INCOMING_COMMAND_TYPE);
        return INCOMING_COMMAND_TYPE ^ (IS_PRODUCT ? PRODUCT : GROUP);
    }

    static String getTypeCommand(int INCOMING_COMMAND_TYPE) throws Exception {
        int COMMAND = getTypeCommandCode(INCOMING_COMMAND_TYPE);
        switch (COMMAND) {
            case CREATE:
                return "CREATE";

            case READ:
                return "READ";

            case UPDATE:
                return "UPDATE";

            case DELETE:
                return "DELETE";

            case LIST_BY_CRITERIA:
                return "LIST_BY_CRITERIA";

            default:
                throw new Exception("Undefined INCOMING_COMMAND_TYPE");
        }
    }

    @Getter
    boolean isProduct;

    @Getter
    int commandTypeCode;

    @Getter
    String commandType;

    public CommandTypeEncoder(int INCOMING_COMMAND_TYPE) throws Exception {
        isProduct = isProduct(INCOMING_COMMAND_TYPE);
        commandTypeCode = getTypeCommandCode(INCOMING_COMMAND_TYPE);
        commandType = getTypeCommand(INCOMING_COMMAND_TYPE);
    }
}
