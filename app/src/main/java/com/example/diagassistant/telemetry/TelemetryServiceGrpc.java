package com.example.diagassistant.telemetry;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler",
    comments = "Source: telemetry.proto")
public final class TelemetryServiceGrpc {

  private TelemetryServiceGrpc() {}

  public static final String SERVICE_NAME = "telemetry.TelemetryService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<Empty,
      TelemetrySample> getStreamTelemetryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StreamTelemetry",
      requestType = Empty.class,
      responseType = TelemetrySample.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<Empty,
      TelemetrySample> getStreamTelemetryMethod() {
    io.grpc.MethodDescriptor<Empty, TelemetrySample> getStreamTelemetryMethod;
    if ((getStreamTelemetryMethod = TelemetryServiceGrpc.getStreamTelemetryMethod) == null) {
      synchronized (TelemetryServiceGrpc.class) {
        if ((getStreamTelemetryMethod = TelemetryServiceGrpc.getStreamTelemetryMethod) == null) {
          TelemetryServiceGrpc.getStreamTelemetryMethod = getStreamTelemetryMethod = 
              io.grpc.MethodDescriptor.<Empty, TelemetrySample>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "telemetry.TelemetryService", "StreamTelemetry"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  TelemetrySample.getDefaultInstance()))
                  .setSchemaDescriptor(new TelemetryServiceMethodDescriptorSupplier("StreamTelemetry"))
                  .build();
          }
        }
     }
     return getStreamTelemetryMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TelemetryServiceStub newStub(io.grpc.Channel channel) {
    return new TelemetryServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TelemetryServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new TelemetryServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TelemetryServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new TelemetryServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class TelemetryServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void streamTelemetry(Empty request,
                                io.grpc.stub.StreamObserver<TelemetrySample> responseObserver) {
      asyncUnimplementedUnaryCall(getStreamTelemetryMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getStreamTelemetryMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                Empty,
                TelemetrySample>(
                  this, METHODID_STREAM_TELEMETRY)))
          .build();
    }
  }

  /**
   */
  public static final class TelemetryServiceStub extends io.grpc.stub.AbstractStub<TelemetryServiceStub> {
    private TelemetryServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TelemetryServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected TelemetryServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TelemetryServiceStub(channel, callOptions);
    }

    /**
     */
    public void streamTelemetry(Empty request,
                                io.grpc.stub.StreamObserver<TelemetrySample> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getStreamTelemetryMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class TelemetryServiceBlockingStub extends io.grpc.stub.AbstractStub<TelemetryServiceBlockingStub> {
    private TelemetryServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TelemetryServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected TelemetryServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TelemetryServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<TelemetrySample> streamTelemetry(
        Empty request) {
      return blockingServerStreamingCall(
          getChannel(), getStreamTelemetryMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class TelemetryServiceFutureStub extends io.grpc.stub.AbstractStub<TelemetryServiceFutureStub> {
    private TelemetryServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TelemetryServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected TelemetryServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TelemetryServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_STREAM_TELEMETRY = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TelemetryServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TelemetryServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_STREAM_TELEMETRY:
          serviceImpl.streamTelemetry((Empty) request,
              (io.grpc.stub.StreamObserver<TelemetrySample>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class TelemetryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TelemetryServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Telemetry.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("TelemetryService");
    }
  }

  private static final class TelemetryServiceFileDescriptorSupplier
      extends TelemetryServiceBaseDescriptorSupplier {
    TelemetryServiceFileDescriptorSupplier() {}
  }

  private static final class TelemetryServiceMethodDescriptorSupplier
      extends TelemetryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    TelemetryServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (TelemetryServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TelemetryServiceFileDescriptorSupplier())
              .addMethod(getStreamTelemetryMethod())
              .build();
        }
      }
    }
    return result;
  }
}
