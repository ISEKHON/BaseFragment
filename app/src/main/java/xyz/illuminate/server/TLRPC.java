package xyz.illuminate.server;

public class TLRPC {

    public static abstract class MessageEntity extends TLObject {
        public int offset;
        public int length;
        public String url;
        public String language;

        public static MessageEntity TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            MessageEntity result = null;
            switch (constructor) {
                case 0x76a6d327:
                    result = new TL_messageEntityTextUrl();
                    break;
                case 0x64e475c2:
                    result = new TL_messageEntityEmail();
                    break;
                case 0x73924be0:
                    result = new TL_messageEntityPre();
                    break;
                case 0xbb92ba95:
                    result = new TL_messageEntityUnknown();
                    break;
                case 0x6ed02538:
                    result = new TL_messageEntityUrl();
                    break;
                case 0x826f8b60:
                    result = new TL_messageEntityItalic();
                    break;
                case 0xfa04579d:
                    result = new TL_messageEntityMention();
                    break;
                case 0x32ca960f:
                    result = new TL_messageEntitySpoiler();
                    break;
                case 0xbd610bc9:
                    result = new TL_messageEntityBold();
                    break;
                case 0x6f635b0d:
                    result = new TL_messageEntityHashtag();
                    break;
                case 0x28a20571:
                    result = new TL_messageEntityCode();
                    break;
                case 0xbf0693d4:
                    result = new TL_messageEntityStrike();
                    break;
                case 0x20df5d0:
                    result = new TL_messageEntityBlockquote();
                    break;
                case 0x9c4e7e8b:
                    result = new TL_messageEntityUnderline();
                    break;
                case 0x9b69e34b:
                    result = new TL_messageEntityPhone();
                    break;
                case 0xdc7b1140:
                    result = new TL_messageEntityMentionName();
                    break;
            }
            if (result == null && exception) {
                throw new RuntimeException(String.format("can't parse magic %x in MessageEntity", constructor));
            }
            if (result != null) {
                result.readParams(stream, exception);
            }
            return result;
        }
    }

    public static class TL_messageEntityTextUrl extends MessageEntity {
        public static int constructor = 0x76a6d327;


        public void readParams(AbstractSerializedData stream, boolean exception) {
            offset = stream.readInt32(exception);
            length = stream.readInt32(exception);
            url = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(offset);
            stream.writeInt32(length);
            stream.writeString(url);
        }
    }

    public static class TL_messageEntityEmail extends MessageEntity {
        public static int constructor = 0x64e475c2;


        public void readParams(AbstractSerializedData stream, boolean exception) {
            offset = stream.readInt32(exception);
            length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(offset);
            stream.writeInt32(length);
        }
    }

    public static class TL_messageEntityPre extends MessageEntity {
        public static int constructor = 0x73924be0;


        public void readParams(AbstractSerializedData stream, boolean exception) {
            offset = stream.readInt32(exception);
            length = stream.readInt32(exception);
            language = stream.readString(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(offset);
            stream.writeInt32(length);
            stream.writeString(language);
        }
    }

    public static class TL_messageEntityUnknown extends MessageEntity {
        public static int constructor = 0xbb92ba95;


        public void readParams(AbstractSerializedData stream, boolean exception) {
            offset = stream.readInt32(exception);
            length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(offset);
            stream.writeInt32(length);
        }
    }

    public static class TL_messageEntityUrl extends MessageEntity {
        public static int constructor = 0x6ed02538;


        public void readParams(AbstractSerializedData stream, boolean exception) {
            offset = stream.readInt32(exception);
            length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(offset);
            stream.writeInt32(length);
        }
    }

    public static class TL_messageEntityItalic extends MessageEntity {
        public static int constructor = 0x826f8b60;


        public void readParams(AbstractSerializedData stream, boolean exception) {
            offset = stream.readInt32(exception);
            length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(offset);
            stream.writeInt32(length);
        }
    }

    public static class TL_messageEntityMention extends MessageEntity {
        public static int constructor = 0xfa04579d;


        public void readParams(AbstractSerializedData stream, boolean exception) {
            offset = stream.readInt32(exception);
            length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(offset);
            stream.writeInt32(length);
        }
    }

    public static class TL_messageEntitySpoiler extends MessageEntity {
        public static int constructor = 0x32ca960f;

        public void readParams(AbstractSerializedData stream, boolean exception) {
            offset = stream.readInt32(exception);
            length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(offset);
            stream.writeInt32(length);
        }
    }

    public static class TL_messageEntityBold extends MessageEntity {
        public static int constructor = 0xbd610bc9;


        public void readParams(AbstractSerializedData stream, boolean exception) {
            offset = stream.readInt32(exception);
            length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(offset);
            stream.writeInt32(length);
        }
    }

    public static class TL_messageEntityHashtag extends MessageEntity {
        public static int constructor = 0x6f635b0d;


        public void readParams(AbstractSerializedData stream, boolean exception) {
            offset = stream.readInt32(exception);
            length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(offset);
            stream.writeInt32(length);
        }
    }

    public static class TL_messageEntityCode extends MessageEntity {
        public static int constructor = 0x28a20571;


        public void readParams(AbstractSerializedData stream, boolean exception) {
            offset = stream.readInt32(exception);
            length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(offset);
            stream.writeInt32(length);
        }
    }

    public static class TL_messageEntityStrike extends MessageEntity {
        public static int constructor = 0xbf0693d4;


        public void readParams(AbstractSerializedData stream, boolean exception) {
            offset = stream.readInt32(exception);
            length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(offset);
            stream.writeInt32(length);
        }
    }

    public static class TL_messageEntityBlockquote extends MessageEntity {
        public static int constructor = 0x20df5d0;


        public void readParams(AbstractSerializedData stream, boolean exception) {
            offset = stream.readInt32(exception);
            length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(offset);
            stream.writeInt32(length);
        }
    }

    public static class TL_messageEntityUnderline extends MessageEntity {
        public static int constructor = 0x9c4e7e8b;


        public void readParams(AbstractSerializedData stream, boolean exception) {
            offset = stream.readInt32(exception);
            length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(offset);
            stream.writeInt32(length);
        }
    }

    public static class TL_messageEntityPhone extends MessageEntity {
        public static int constructor = 0x9b69e34b;

        public void readParams(AbstractSerializedData stream, boolean exception) {
            offset = stream.readInt32(exception);
            length = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(offset);
            stream.writeInt32(length);
        }
    }

    public static class TL_messageEntityMentionName extends MessageEntity {
        public static int constructor = 0xdc7b1140;

        public long user_id;

        public void readParams(AbstractSerializedData stream, boolean exception) {
            offset = stream.readInt32(exception);
            length = stream.readInt32(exception);
            user_id = stream.readInt64(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(offset);
            stream.writeInt32(length);
            stream.writeInt64(user_id);
        }
    }

}