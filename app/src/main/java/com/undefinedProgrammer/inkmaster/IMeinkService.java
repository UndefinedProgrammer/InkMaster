package com.undefinedProgrammer.inkmaster;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IMeinkService extends IInterface {
    void disableCustomDisplayMode(String str) throws RemoteException;

    void lockCustomDisplayMode(String str) throws RemoteException;

    void setCustomDisplayMode(String str, String str2, String str3, String str4, float f, float f2) throws RemoteException;

    void setDisplayMode(String str, int i) throws RemoteException;

    void setDisplayModeFromPackage(String str) throws RemoteException;

    public static class Default implements IMeinkService {
        @Override
        public void setDisplayModeFromPackage(String packageName) throws RemoteException {
        }

        @Override
        public void lockCustomDisplayMode(String packageName) throws RemoteException {
        }

        @Override
        public void disableCustomDisplayMode(String packageName) throws RemoteException {
        }

        @Override
        public void setCustomDisplayMode(String packageName, String waveformMode, String bpp, String gamma, float brightness, float contrast) throws RemoteException {
        }

        @Override
        public void setDisplayMode(String packageName, int mode) throws RemoteException {
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IMeinkService {
        public static final String DESCRIPTOR = "android.meink.IMeinkService";
        static final int TRANSACTION_disableCustomDisplayMode = 3;
        static final int TRANSACTION_lockCustomDisplayMode = 2;
        static final int TRANSACTION_setCustomDisplayMode = 4;
        static final int TRANSACTION_setDisplayMode = 5;
        static final int TRANSACTION_setDisplayModeFromPackage = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMeinkService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMeinkService)) {
                return (IMeinkService) iin;
            }
            return new Proxy(obj);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "setDisplayModeFromPackage";
                case 2:
                    return "lockCustomDisplayMode";
                case 3:
                    return "disableCustomDisplayMode";
                case 4:
                    return "setCustomDisplayMode";
                case 5:
                    return "setDisplayMode";
                default:
                    return null;
            }
        }

        //@Override // android.os.Binder

        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }


        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            data.enforceInterface(DESCRIPTOR);
                            String _arg0 = data.readString();
                            setDisplayModeFromPackage(_arg0);
                            reply.writeNoException();
                            return true;
                        case 2:
                            data.enforceInterface(DESCRIPTOR);
                            String _arg02 = data.readString();
                            lockCustomDisplayMode(_arg02);
                            reply.writeNoException();
                            return true;
                        case 3:
                            data.enforceInterface(DESCRIPTOR);
                            String _arg03 = data.readString();
                            disableCustomDisplayMode(_arg03);
                            reply.writeNoException();
                            return true;
                        case 4:
                            data.enforceInterface(DESCRIPTOR);
                            String _arg04 = data.readString();
                            String _arg1 = data.readString();
                            String _arg2 = data.readString();
                            String _arg3 = data.readString();
                            float _arg4 = data.readFloat();
                            float _arg5 = data.readFloat();
                            setCustomDisplayMode(_arg04, _arg1, _arg2, _arg3, _arg4, _arg5);
                            reply.writeNoException();
                            return true;
                        case 5:
                            data.enforceInterface(DESCRIPTOR);
                            String _arg05 = data.readString();
                            int _arg12 = data.readInt();
                            setDisplayMode(_arg05, _arg12);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        private static class Proxy implements IMeinkService {
            public static IMeinkService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override
            public void setDisplayModeFromPackage(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().setDisplayModeFromPackage(packageName);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void lockCustomDisplayMode(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().lockCustomDisplayMode(packageName);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void disableCustomDisplayMode(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().disableCustomDisplayMode(packageName);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setCustomDisplayMode(String packageName, String waveformMode, String bpp, String gamma, float brightness, float contrast) {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                } catch (Throwable th) {

                }
                try {
                    _data.writeString(packageName);
                } catch (Throwable th2) {

                }
                try {
                    _data.writeString(waveformMode);
                } catch (Throwable th3) {

                }
                try {
                    _data.writeString(bpp);
                } catch (Throwable th4) {
                }
                try {
                    _data.writeString(gamma);
                } catch (Throwable th5) {

                }
                try {
                    _data.writeFloat(brightness);
                } catch (Throwable th6) {

                }
                try {
                    _data.writeFloat(contrast);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().setCustomDisplayMode(packageName, waveformMode, bpp, gamma, brightness, contrast);
                        _reply.recycle();
                        _data.recycle();
                    } else {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                    }
                } catch (Throwable th7) {

                }
            }

            @Override
            public void setDisplayMode(String packageName, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(mode);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().setDisplayMode(packageName, mode);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IMeinkService impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IMeinkService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
